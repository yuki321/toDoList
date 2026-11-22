package todolist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;

import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import todolist.common.Logger;

@Entity
@Table(name = "users")
public class ProfileImage {

	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
			

	/**
	 * S3の署名付きURLを生成するメソッド
	 * @param String region
	 * @param String bucketName
	 * @param String objectKey
	 * @return String presignedUrl
	 */
	public String createViewPresignedUrl(final String region, final String bucketName, final String objectKey) {
		
	    try (S3Presigner presigner = S3Presigner.builder()
	            .region(Region.of(region))
	            .build()) {

	        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
	            .bucket(bucketName)
	            .key(objectKey)
	            .build();

	        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
	            .signatureDuration(Duration.ofMinutes(10)) // 表示用に10分など設定
	            .getObjectRequest(getObjectRequest)
	            .build();

	        return presigner.presignGetObject(presignRequest).url().toString();
	    
	    }catch (Exception e) {
			Logger.log(CLASS_NAME, "[Error] ProfileImage: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}
	
	
	/**
	 * S3の署名付きURLを生成するメソッド（アップロード用）
	 * @param String bucketName
	 * @param String objectKey
	 * @return String presignedUrl
	 */
	public String createPresignedUrl(final String bucketName, final String objectKey, final String contentType) {
        try (S3Presigner presigner = S3Presigner.create()) {
            // Create an override configuration to store the query parameters.
            AwsRequestOverrideConfiguration.Builder overrideConfigurationBuilder = AwsRequestOverrideConfiguration.builder();

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .overrideConfiguration(overrideConfigurationBuilder.build()) 
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL expires in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String myURL = presignedRequest.url().toString();

            Logger.log(CLASS_NAME, "Presigned URL: " + myURL);

            return presignedRequest.url().toExternalForm();
        }
        
	}
		
}
