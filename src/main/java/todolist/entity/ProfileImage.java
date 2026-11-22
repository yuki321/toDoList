package todolist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import todolist.common.Logger;

@Entity
@Table(name = "users")
public class ProfileImage {

	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
			

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


}
