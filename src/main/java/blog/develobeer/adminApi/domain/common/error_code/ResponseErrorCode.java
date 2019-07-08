package blog.develobeer.adminApi.domain.common.error_code;

public class ResponseErrorCode {
    public static class Post{
        public static final int FILE_NOT_EXIST = -1001;
        public static final int DESTINATION_FOLDER_CREATION_FAIL = -1002;
        public static final int UPLOAD_FAIL = -1003;
    }
}
