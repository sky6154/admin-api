
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

node {
    try{
        stage('Checkout'){
            checkout scm
        }

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
                stage('docker-compose build & save image'){
                    sh "docker-compose build"
                    sh "docker save -o admin-api.tar admin-api:latest"
                }

                def deployWorkerList = []

                if("${env.CURRENT_BACK_ENV}" == "blue"){
                    deployWorkerList.add("GreenB2")

                    stage('deploy swarm worker'){
                        def stepsForParallel = deployWorkerList.collectEntries {
                            ["${it}" : deployWorker(it)]
                        }
                        parallel stepsForParallel
                    }

                    stage('deploy swarm manager'){
                        deployManager("GreenB1")
                    }

                    stage('overwrite env'){
                        overwriteEnv("green")
                    }

                    stage('overwrite nginx conf'){
                        sh "docker cp /var/deploy_env_conf/admin_green_back.conf myNginx:/etc/nginx/conf.d/target_admin_back.conf"
                    }

                    stage('reload nginx'){
                        sh "docker kill -s HUP myNginx"
                    }
                }
                else{
                    deployWorkerList.add("BlueB2")

                    stage('deploy swarm worker'){
                        def stepsForParallel = deployWorkerList.collectEntries {
                            ["${it}" : deployWorker(it)]
                        }
                        parallel stepsForParallel
                    }

                    stage('deploy swarm manager'){
                        deployManager("BlueB1")
                    }

                    stage('overwrite env'){
                        overwriteEnv("blue")
                    }

                    stage('overwrite nginx conf'){
                        sh "docker cp /var/deploy_env_conf/admin_blue_back.conf myNginx:/etc/nginx/conf.d/target_admin_back.conf"
                    }

                    stage('reload nginx'){
                        sh "docker kill -s HUP myNginx"
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


def deployManager(configName){
    sshPublisher(publishers: [
            sshPublisherDesc(
                    configName: configName,
                    transfers: [
                            sshTransfer(sourceFiles: 'admin-api.tar, deploy-admin-manager.sh',
                                    execCommand: "cd /root && \
                                  chmod 744 ./deploy-admin-manager.sh && \
                                  ./deploy-admin-manager.sh")
                    ],
            )
    ])
}

def deployWorker(configName){
    // We need to wrap what we return in a Groovy closure, or else it's invoked
    // when this method is called, not when we pass it to parallel.
    // To do this, you need to wrap the code below in { }, and either return
    // that explicitly, or use { -> } syntax.
    return {
        sshPublisher(publishers: [
                sshPublisherDesc(
                        configName: configName,
                        transfers: [
                                sshTransfer(sourceFiles: 'admin-api.tar, deploy-admin-worker.sh',
                                        execCommand: "cd /root && \
                                    chmod 744 ./deploy-admin-worker.sh && \
                                    ./deploy-admin-worker.sh")
                        ],
                )
        ])
    }
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