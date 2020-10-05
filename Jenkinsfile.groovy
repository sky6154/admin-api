import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

node {
    def NAME = "admin-api"
    def DOCKER_REPO = "hub.develobeer.blog"

    try{
        stage('Checkout'){
            checkout scm
        }

        def shortRevision = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
        println("short revision : " + shortRevision)

        stage('Copy application.yml'){
            if(!fileExists('./src/main/resources')){
                sh "mkdir ./src/main/resources"
            }

            sh "cp -rf /var/admin_config/application.yml ./src/main/resources/"
        }

        stage('Build Gradle'){
            sh "chmod +x gradlew"
            sh "./gradlew build"
        }

        switch(params.JOB){
            case "build&deploy":
                stage("docker build with tag") {
                    sh "docker build . -t ${DOCKER_REPO}/${NAME}:${shortRevision}"
                }

                stage("docker login & image push") {
                    sh "docker login hub.develobeer.blog -u ${params.DOCKER_REPO_USER} -p ${params.DOCKER_REPO_PASS}"
                    sh "docker push ${DOCKER_REPO}/${NAME}:${shortRevision}"
                }

                if("${env.CURRENT_ADMIN_BACK_ENV}" == "blue"){
                    stage('deploy swarm manager'){
                        deployManager("GreenB1", shortRevision)
                    }

                    if (currentBuild.result == "SUCCESS") {
                        stage('overwrite env') {
                            overwriteEnv("green")
                        }

                        stage('overwrite nginx conf') {
                            sh "docker cp /var/deploy_env_conf/admin_green_back.conf myNginx:/etc/nginx/conf.d/target_admin_back.conf"
                        }

                        stage('reload nginx') {
                            sh "docker kill -s HUP myNginx"
                        }
                    }
                }
                else{ // green
                    stage('deploy swarm manager'){
                        deployManager("BlueB1", shortRevision)
                    }

                    if (currentBuild.result == "SUCCESS") {
                        stage('overwrite env') {
                            overwriteEnv("blue")
                        }

                        stage('overwrite nginx conf') {
                            sh "docker cp /var/deploy_env_conf/admin_blue_back.conf myNginx:/etc/nginx/conf.d/target_admin_back.conf"
                        }

                        stage('reload nginx') {
                            sh "docker kill -s HUP myNginx"
                        }
                    }
                }

                break
        }
    }
    catch (err){
        currentBuild.result = 'FAILED'
        println(err.getMessage())
        throw err
    }
}


def deployManager(configName, shortRevision) {
    sshPublisher(publishers: [
            sshPublisherDesc(
                    configName: configName,
                    transfers: [
                            sshTransfer(sourceFiles: 'docker-compose-admin.yml, deploy-admin-manager.sh',
                                    execCommand: "cd /root && \
                                    docker login hub.develobeer.blog -u ${params.DOCKER_REPO_USER} -p ${params.DOCKER_REPO_PASS} && \
                                    chmod 744 ./deploy-admin-manager.sh && \
                                    ./deploy-admin-manager.sh ${shortRevision}")
                    ],
            )
    ],
            failOnError: true)
}

def overwriteEnv(activeEnv){
    Jenkins instance = Jenkins.getInstance()
    def globalNodeProperties = instance.getGlobalNodeProperties()
    def envVarsNodePropertyList = globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)
    def newEnvVarsNodeProperty = null
    def envVars = null

    if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
        newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
        globalNodeProperties.add(newEnvVarsNodeProperty)
        envVars = newEnvVarsNodeProperty.getEnvVars()
    }
    else {
        envVars = envVarsNodePropertyList.get(0).getEnvVars()
    }

    envVars.put("CURRENT_ADMIN_BACK_ENV", activeEnv)

    instance.save()
}