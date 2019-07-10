package blog.develobeer.adminApi.utils;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class CommonTemplateMethod {
    public static <T, ID> boolean simpleSaveTryCatchBooleanReturn(JpaRepository<T, ID> repo, T model){
        try{
            repo.saveAndFlush(model);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static <T, ID> boolean simpleSaveTryCatchBooleanReturn(JpaRepository<T, ID> repo, List<T> modelList){
        try{
            repo.saveAll(modelList);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static <T, ID> boolean simpleDeleteTryCatchBooleanReturn(JpaRepository<T, ID> repo, T model){
        try{
            repo.delete(model);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
