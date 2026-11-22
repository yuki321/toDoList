package todolist.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import todolist.common.Logger;
import todolist.entity.Pager;
import todolist.entity.ProfileImage;
import todolist.entity.User;
import todolist.repository.UserRepository;

@Service
@Transactional
public class UserScenario {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private ToDoServiceIF toDoService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${aws.s3.region}")
	private String region;
	
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	@Value("${aws.s3.default-image-key}")
	private String objectKey;
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	
	/**
	 * ユーザー情報を全件取得
	 * @param UserDetails userDetails
	 * @param Model model
	 * @param Pageable pageable
	 * @param int page
	 * @return String
	 */
	public String getAllUsersData(
			final UserDetails userDetails,
			final Model model, 
			final Pageable pageable, 
			final int page) {
		
		final Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		final String role = (String) userIfo.get("role");
		
		if(!role.equals("Admin")) {
			return "redirect:/";
		}
		
		
		final List<User> users = userService.getAllUsers();
		final Page<User> usersPerPage = userService.getAllUsers(pageable);
		
		// 表示するユーザーデータ
		model.addAttribute("users", usersPerPage.getContent());
		model.addAttribute("userCount", users.size());
		model.addAttribute("user", new User());

		// ページャー関連のデータ
		final Pager pager = new Pager();
		int topIndex = pager.getTopIndex(page, usersPerPage.getSize());
		int lastIndex = pager.getLastIndex(page, usersPerPage.getSize());
		
		model.addAttribute("pages", usersPerPage);
		model.addAttribute("topIndex", topIndex);
		model.addAttribute("lastIndex", lastIndex);
		
		// データ件数・ページャーの表示
		model.addAttribute("display", true);
		
		
		return "";
	}
	
	
	/**
	 * S3からデフォルト画像のURLを取得
	 * @return String
	 */
	public String getS3PresignedUrl() {
		ProfileImage profileImage = new ProfileImage();
		String presignedUrl = profileImage.createViewPresignedUrl(region, bucketName, objectKey);
		if(presignedUrl != null) {
			return presignedUrl;
		} else {
			Logger.log(CLASS_NAME, "[Error] getS3PresignedUrl: プリサインドURLの生成に失敗しました。");
			return null;
		}
	}
	
	
	/**
	 * S3の署名付きURLを返すためのレスポンス用レコード
	 * @param String presignedUrl
	 * @param String keyName
	 * @return PresignedUrlResponse
	 */
	public record PresignedUrlResponse(String presignedUrl, String keyName) {}
	
	
	/**
	 * S3の署名付きURLを生成するメソッド（アップロード用）
	 * @param Long userId
	 * @param String originalFileName
	 * @return PresignedUrlResponse
	 */
	public PresignedUrlResponse generateUploadUrl(final Long userId, final String originalFileName,
			final String contentType) {
        
		String fileName = extractImageFileName(originalFileName);
		if (contentType == null || contentType.isBlank()) {
			throw new IllegalArgumentException("画像のContent-Typeがありません");
		}
		
        String keyName = "images/" + userId + "/" + fileName;

		ProfileImage profileImage = new ProfileImage();
        String presignedUrl = profileImage.createPresignedUrl(
        		bucketName, 
                keyName,
                contentType
        );

        // フロントエンドには「アップロード用URL」と「後でDB保存に使うキー」を返す。
        // キーはフロントエンドでDB保存する際に使うため、フロントエンドに返す
        return new PresignedUrlResponse(presignedUrl, keyName);
    }


	/**
	 * S3アップロード完了後、画像ファイル名を users.s3_image_url に保存する
	 * @param Long userId
	 * @param String keyName
	 */
	public void saveS3ImageFileName(final Long userId, final String keyName) {

		String prefix = "images/" + userId + "/";
		if (keyName == null) {
			throw new IllegalArgumentException("画像ファイル名が不正です");
		}

		String fileName = extractImageFileName(keyName.substring(prefix.length()));
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
		user.setS3ImageUrl(fileName);
		user.setUpdatedAt(LocalDateTime.now());
		userRepository.save(user);
		Logger.log(CLASS_NAME, "saveS3ImageFileName: 画像ファイル名を保存しました。fileName=" + fileName);
	}


	/**
	 * オブジェクトキー、または元ファイル名から画像ファイル名（xxx.png など）を取り出す
	 * @param String value
	 * @return String
	 */
	private String extractImageFileName(final String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("画像ファイル名がありません");
		}

		String name = value.replace('\\', '/');
		int slash = name.lastIndexOf('/');
		if (slash >= 0) {
			name = name.substring(slash + 1);
		}

		int underscore = name.indexOf('_');
		if (underscore >= 0 && underscore < name.length() - 1) {
			String prefix = name.substring(0, underscore);
			if (prefix.length() == 36 && prefix.chars().filter(ch -> ch == '-').count() == 4) {
				name = name.substring(underscore + 1);
			}
		}

		if (name.isBlank() || name.contains("..") || name.indexOf('/') >= 0) {
			throw new IllegalArgumentException("画像ファイル名が不正です");
		}
		return name;
	}
	
	
	/**
	 * ユーザーIDからS3の画像URLを取得
	 * @param id
	 * @return
	 */
	public String getS3UrlFromDb(final Long id) {
		return userService.getS3ImageUrlByUserId(id);
	}
	
	
	/**
	 * ユーザー情報を検索
	 * @param User user
	 * @param Model model
	 * @param Pageable pageable
	 * @return String
	 */
	public String searchUsersData(
			final User user, 
			final Model model, 
			final Pageable pageable) {
		
		final Page<User> users = userService.searchUsers(user, pageable);
		final int userCount = users.getContent().size();


		if(userCount < 0) return "redirect:/api/users/user?page=0";

		model.addAttribute("users", users.getContent());
		model.addAttribute("user", new User());
		model.addAttribute("userCount", userCount);
		
		
		// ページャー関連のデータ
		final Pager pager = new Pager();
		final int topIndex = pager.getTopIndex(0, users.getSize());
		final int lastIndex = pager.getLastIndex(0, users.getSize());
		
		model.addAttribute("pages", users);
		model.addAttribute("topIndex", topIndex);
		model.addAttribute("lastIndex", lastIndex);
		
		// データ件数・ページャーの非表示
		model.addAttribute("display", false);
		
		return "";
	}
	
}


