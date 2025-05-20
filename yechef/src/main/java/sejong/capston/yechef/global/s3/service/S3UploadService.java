package sejong.capston.yechef.global.s3.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Image.repository.ImageRepository;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3UploadService {

  private final S3Client s3Client;
  private final ImageRepository imageRepository;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${cloud.aws.region.static}")
  private String region;

  /**
   * S3에 파일 업로드 후 URL 반환
   */
  public String uploadFile(MultipartFile file) {
    String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
    String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;

    try (InputStream inputStream = file.getInputStream()) {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .contentType(file.getContentType())
          .acl(ObjectCannedACL.PUBLIC_READ)
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

      return fileUrl;

    } catch (IOException e) {
      throw BaseException.from(ErrorCode.FILE_UPLOAD_FAIL, "파일 업로드 중 IO 오류: " + e.getMessage());
    } catch (Exception e) {
      throw BaseException.from(ErrorCode.FILE_UPLOAD_FAIL, "파일 업로드 실패: " + e.getMessage());
    }
  }

  /**
   * S3에 업로드하고 Image 엔티티로 DB에 저장
   */
  public String uploadAndSave(MultipartFile file, Recipe recipe) {
    String url = uploadFile(file);
    imageRepository.save(Image.builder()
        .s3Url(url)
        .recipe(recipe)
        .build());
    return url;
  }

  public void deleteFile(String s3Key) {
    try {
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .build();
      s3Client.deleteObject(deleteObjectRequest);

    } catch (Exception e) {
      throw BaseException.from(ErrorCode.FILE_DELETE_FAIL, e.getMessage());
    }
  }

  public String uploadWithKey(MultipartFile file, String fileKey) {
    String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileKey;

    try (InputStream inputStream = file.getInputStream()) {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileKey)
          .contentType(file.getContentType())
          .acl(ObjectCannedACL.PUBLIC_READ)
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

      return fileUrl;

    } catch (IOException e) {
      throw BaseException.from(ErrorCode.FILE_UPLOAD_FAIL, "파일 업로드 중 IO 오류: " + e.getMessage());
    } catch (Exception e) {
      throw BaseException.from(ErrorCode.FILE_UPLOAD_FAIL, "파일 업로드 실패: " + e.getMessage());
    }
  }

  public String uploadAndGenerateKey(MultipartFile file) {
    String key = generateS3Key(file);
    return uploadWithKey(file, key); // 내부 재사용
  }

  private String generateS3Key(MultipartFile file) {
    return "source-images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
  }

  public String extractKeyFromUrl(String url) {
    if (url == null || !url.contains(".amazonaws.com/")) return null;
    return url.substring(url.indexOf(".com/") + 5);
  }

}