package todolist.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import todolist.common.Logger;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import todolist.entity.PasswordChange;
import todolist.entity.ProfileImage;
import todolist.entity.User;
import todolist.service.ToDoServiceIF;
import todolist.service.UserScenario;
import todolist.service.UserScenario.PresignedUrlResponse;
import todolist.service.UserServiceIF;
import todolist.service.UserUpdateServiceIF;

@Controller
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private UserUpdateServiceIF userUpdateService;
	
	@Autowired
	private ToDoServiceIF toDoService;
	
	@Autowired
	private UserScenario userScenario;
	
	@Value("${aws.s3.region}")
	private String region;
	
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	@Value("${aws.s3.default-image-key}")
	private String objectKey = "defaultUserImage.png";
	
		
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @param Model model
	 * @param Pageable pageable
	 * @param int page Xページ目（インデックスは0始まり）
	 * @return String
	 */
	@GetMapping("user")
	public String getAllUsers(
			@AuthenticationPrincipal final UserDetails userDetails, 
			final Model model, 
			@PageableDefault(page = 0, size = 10) final Pageable pageable, 
			@RequestParam(name = "page") final int page){
		
		Logger.log(CLASS_NAME, "getAllUsers: ユーザー一覧表示");
		
		userScenario.getAllUsersData(userDetails, model, pageable, page);
		
		return "user";
	}
	
	
	/**
	 * ユーザー検索
	 * @param User user
	 * @param Model model
	 * @param Pageable pageable
	 * @return
	 */
	@GetMapping("search")
	public String searchUsers(
			@ModelAttribute("user") final User user, 
			final Model model, 
			@PageableDefault(page = 0, size = 10) final Pageable pageable){

		Logger.log(CLASS_NAME, "searchUsers: ユーザー検索");
		
		userScenario.searchUsersData(user, model, pageable);
		
		return "user";
	}
	
	
	/**
	 * IDで指定したユーザーを取得
	 * @param Long id
	 * @param Madel model
	 * @return String
	 */
	@GetMapping("user/{id}")
	public String getUserById(@PathVariable final Long id, final Model model){
		
		Logger.log(CLASS_NAME, "getUserById: ユーザー詳細表示");
		// idがLong型でない場合
		if(!(id instanceof Long)) {
			Logger.log(CLASS_NAME, "[Error] getUserById: idがLong型ではありません。");
			return "redirect:/api/users/user?page=0";
		}

		try {
			final Optional<User> user = userService.getUserById(id);
			
			if(user.isPresent()) {
				
				model.addAttribute("user", user.orElse(null));
	            Logger.log(CLASS_NAME, "getUserById: 指定したIDのユーザーを取得しました。");

	            /**
	             * DBにS3 image URLが保存されている場合はそれを使用し、
	             * 保存されていない場合はS3から署名付きURLを生成して使用する
	             */

	            // DBにS3 image URLが保存されている場合はそれを使用する
	            String S3ImageUrlFromDb = userService.getS3ImageUrlByUserId(id);
	            Logger.log(CLASS_NAME, "__S3ImageUrlFromDb: " + S3ImageUrlFromDb);
	            Logger.log(CLASS_NAME, "__id: " + id);
	            Logger.log(CLASS_NAME, "__objectKey: " + objectKey);	            
	            
	            ProfileImage profileImage = new ProfileImage();	
	            String presignedUrl = profileImage.createViewPresignedUrl(region, bucketName, objectKey);
	            
	            if(S3ImageUrlFromDb != null) {
	            	
	            	Logger.log(CLASS_NAME, "__presignedUrl: " + presignedUrl);
	            	
					objectKey = "images/" + id + "/" + S3ImageUrlFromDb;
					presignedUrl = profileImage.createViewPresignedUrl(region, bucketName, objectKey);

	            	Logger.log(CLASS_NAME, "getUserById: DBにS3 image URLが保存されているため、それを使用します。");
	            	Logger.log(CLASS_NAME, "getUserById: presignedUrl=" + presignedUrl);
	            	model.addAttribute("profileImageUrl", presignedUrl);
	            	
	            	// デフォルト設定に戻す（そうしないと他の画像もアップロードした画像が表示される）
	            	objectKey = "defaultUserImage.png";
	            	
	            	return "userDetail";
	            }
	            
	            if(presignedUrl != null) {
	            	
	            	Logger.log(CLASS_NAME, "getUserById: DBにS3 image URLが保存されていないため、S3から署名付きURLを生成して使用します。");
	            	Logger.log(CLASS_NAME, "getUserById: presignedUrl=" + presignedUrl);
	            	model.addAttribute("profileImageUrl", presignedUrl);
	            } else {
	            	Logger.log(CLASS_NAME, "[Error] getUserById: プリサインドURLの生成に失敗しました。");
	            }
	            
				return "userDetail";
			}else {
				Logger.log(CLASS_NAME, "[Error] getUserById: 指定したIDのユーザーが存在しません。");
				return "redirect:/api/users/user?page=0";
			}
			
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, "[Error] getUserById: 指定したIDのユーザーが存在しません。");
			return "redirect:/api/users/user?page=0";
		}
		
	}
	
	
	/**
	 * ユーザー作成画面へ遷移
	 * @param String login(デフォルトはfalse)
	 * @param model
	 * @return String
	 */
	@GetMapping("create")
	public String userCreate(@RequestParam(defaultValue = "false") final String login , final Model model) {
		Logger.log(CLASS_NAME, "userCreate: ユーザー作成画面へ遷移");
		model.addAttribute("user", new User());
		model.addAttribute("login", login);
		return "userCreate";
	}
	

	/**
	 * パスワード変更画面へ遷移
	 * @param Long id
	 * @param Model model
	 * @param User user
	 * @return String
	 */
	@GetMapping("user/{id}/change-password")
	public String changePassword(@PathVariable final Long id, final Model model, @ModelAttribute final User user) {
		Logger.log(CLASS_NAME, "changePassword: パスワード変更画面へ遷移");
		model.addAttribute("user", user);
		
		// changePasswordメソッドで利用
		model.addAttribute("passwordChange", new PasswordChange());
		return "changePassword";
	}
	
	
	/**
	 * ユーザー作成
	 * @param User user
	 * @param Model model
	 * @return String
	 */
	@PostMapping("create")
	public String createUser(@Validated(User.Create.class) @ModelAttribute final User user,
			BindingResult bindingResult, Model model){

		Logger.log(CLASS_NAME, "createUser: ユーザー作成処理開始");
		if (bindingResult.hasErrors()) {
			Logger.log(CLASS_NAME, "[Error] createUser: バリデーションエラーが発生しました。" + bindingResult.getAllErrors());
			return "userCreate";
		}

		try {
			final User createdUser = userUpdateService.createUser(user);
			
			model.addAttribute(createdUser);
			Logger.log(CLASS_NAME, "createUser: ユーザー作成処理が完了しました。");
			return "redirect:/api/users/user?page=0";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.create", e.getMessage());
			Logger.log(CLASS_NAME, "[Error] createUser: ユーザー作成処理中にエラーが発生しました。" + e.getMessage());
			return "userCreate";
		}
		
	}
	
	
	/**
	 * ユーザー更新
	 * @param Long id
	 * @param User user
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	@PostMapping("user/{id}")
	public String updateUser(@PathVariable final Long id,
			@Validated(User.Update.class) @ModelAttribute final User user,
			final BindingResult bindingResult, final Model model){

		Logger.log(CLASS_NAME, "updateUser: ユーザー更新処理開始");
		if (bindingResult.hasErrors()) {
			userService.restoreUserDisplayFields(id, user);
			Logger.log(CLASS_NAME, "[Error] updateUser: バリデーションエラーが発生しました。" + bindingResult.getAllErrors());
			return "userDetail";
		}

		try {
			final User updatedUser = userUpdateService.updateUser(id, user);
			model.addAttribute("user", updatedUser);
			
			Logger.log(CLASS_NAME, "updateUser: ユーザー更新処理が完了しました。");
			return "redirect:/api/users/user?page=0";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.update", e.getMessage());
			userService.restoreUserDisplayFields(id, user);
			Logger.log(CLASS_NAME, "[Error] updateUser: ユーザー更新処理中にエラーが発生しました。" + e.getMessage());
			return "userDetail";
		}
		
	}
	
	
	/**
	 * ユーザー削除
	 * @param Long id
	 * @return String
	 */
	@PostMapping("user/{id}/delete")
    public String deleteUser(@PathVariable final Long id) {
		Logger.log(CLASS_NAME, "deleteUser: ユーザー削除処理開始");
        try {
            userUpdateService.deleteUser(id);
            Logger.log(CLASS_NAME, "deleteUser: ユーザー削除処理が完了しました。");
            return "redirect:/api/users/user?page=0";
        } catch (IllegalArgumentException e) {
        	Logger.log(CLASS_NAME, "[Error] deleteUser: ユーザー削除処理中にエラーが発生しました。" + e.getMessage());
        	return "redirect:/api/users/user?page=0";
        }
    }
	
	
	/**
	 * パスワード変更（再設定とは別）
	 * @param User user
	 * @param Long id
	 * @param UserDetails userDetails
	 * @param PasswordChange passwordChange
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	@PostMapping("user/{id}/change-password")
	public String changePassword(
			final User user,
			@PathVariable final Long id, 
			@AuthenticationPrincipal final UserDetails userDetails,
			@Validated(PasswordChange.PasswordUpdate.class) @ModelAttribute final PasswordChange passwordChange,
			final BindingResult bindingResult,
			final Model model
			) {
		
		Logger.log(CLASS_NAME, "changePassword: パスワード変更処理開始");
		if(bindingResult.hasErrors()) {
			Logger.log(CLASS_NAME, "[Error] changePassword: バリデーションエラーが発生しました。" + bindingResult.getAllErrors());
			return "changePassword";
		}
		
		try {
			
			// 入力したパスワードが現在のパスワードと一致しているか確認
			final List<String> errors = userService.checkPassword(passwordChange, user);
			
			if(!errors.isEmpty()) {
				for(String error: errors) {
					bindingResult.reject("error.passwordChange", error);
					Logger.log(CLASS_NAME, "changePassword: パスワード変更処理中にエラーが発生しました。" + error);
				}
				return "changePassword";
			}
			
			final String newPassword = passwordChange.getNewPassword();
			userUpdateService.savePassword(user, newPassword);
			
			Logger.log(CLASS_NAME, "changePassword: パスワード変更処理が完了しました。");
			return "redirect:/api/users/user?page=0";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, "[Error] changePassword: パスワード変更処理中にエラーが発生しました。" + e.getMessage());
			return "redirect:/api/users/user?page=0";
		}
		
	}
		
	
	/**
	 * CSVアップロード
	 * @param MultipartFile file
	 * @RedirectAttributes redirectAttributes
	 * @return String
	 */
	@PostMapping("/upload")
	public String uploadCsvFile(@RequestParam("file") final MultipartFile file, final RedirectAttributes redirectAttributes) {
		Logger.log(CLASS_NAME, "uploadCsvFile: CSVファイルのアップロード処理開始");
	    try {
	    	final List<String> errors = userUpdateService.uploadCsvFile(file);
	        
	        if(!errors.isEmpty()) {
	        	Logger.log(CLASS_NAME, "[Error] uploadCsvFile: CSVファイルのアップロード処理中にエラーが発生しました。" + errors);
	        	redirectAttributes.addFlashAttribute("CSV_errors", errors);	        	
	        }
	        
	    } catch (Exception e) {
	    	Logger.log(CLASS_NAME, "[Error] uploadCsvFile: CSVファイルのアップロード中にエラーが発生しました。" + e.getMessage());
	    	redirectAttributes.addFlashAttribute("errorMessage", "CSVファイルのインポート中にエラーが発生しました: " + e.getMessage());
	    }
	    
	    Logger.log(CLASS_NAME, "uploadCsvFile: CSVファイルのアップロード処理が完了しました。");
        return "redirect:/api/users/user?page=0";
	}
	
	
	/**
	 * CSVダウンロード
	 * @param model
	 * @return String
	 */
	@PostMapping("/download")
	public String downloadCsvFile(final Model model) {
		Logger.log(CLASS_NAME, "downloadCsvFile: CSVファイルのダウンロード処理開始");
		try {
			userUpdateService.downloadCsvFile();
			Logger.log(CLASS_NAME, "downloadCsvFile: CSVファイルのダウンロード処理が完了しました。");
			return "redirect:/api/users/user?page=0";
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "CSVファイルのダウンロード中にエラーが発生しました: " + e.getMessage());
	
	        final List<User> users = userService.getAllUsers();
			final int userCount = users.size();
	        model.addAttribute("user", new User()); 
	        model.addAttribute("users", users); 
	        model.addAttribute("userCount", userCount); 
	        
	        Logger.log(CLASS_NAME, "[Error] downloadCsvFile: CSVファイルのダウンロード中にエラーが発生しました。" + e.getMessage());
	        return "user";
	    }
		
	}
	
	
	/**
	 * プロフィール画像アップロード前のAPI
	 * フロントエンドに「アップロード用URL」と「後でDB保存に使うキー」を返す
	 * @param Long userId
	 * @param MultipartFile file
	 * @param RedirectAttributes redirectAttributes
	 * @return ResponseEntity
	 * @throws Exception
	 */
	@PostMapping("/user/{id}/presigned-url")
	public ResponseEntity<PresignedUrlResponse> getPresignedUrl(@RequestParam final Long userId,
			@RequestParam("file") final MultipartFile file, 
			final RedirectAttributes redirectAttributes) {
		
		Logger.log(CLASS_NAME, "uploadProfileImage: プロフィール画像のアップロード処理開始");

		PresignedUrlResponse response = null;
		try {
			response = userScenario.generateUploadUrl(userId, file.getOriginalFilename(), file.getContentType());
	        Logger.log(CLASS_NAME, "uploadProfileImage: プロフィール画像のアップロード処理が完了しました。");
	    } catch (Exception e) {
	    	Logger.log(CLASS_NAME, "[Error] uploadProfileImage: プロフィール画像のアップロード中にエラーが発生しました。" + e.getMessage());
	    	redirectAttributes.addFlashAttribute("errorMessage", "プロフィール画像のアップロード中にエラーが発生しました: " + e.getMessage());
	    }
	    
		return ResponseEntity.ok(response);
	}
	
	
	/**
	 * S3へのアップロード完了後、画像ファイル名を users.s3_image_url に保存する
	 * @param Long userId
	 * @param String keyName
	 * @return ResponseEntity
	 */
	@PostMapping("/complete")
    public ResponseEntity<Void> completeUpload(@RequestParam final Long userId,
    		@RequestParam final String keyName) {
		
		Logger.log(CLASS_NAME, "completeUpload: 画像ファイル名の保存を開始します。");
		try {
			userScenario.saveS3ImageFileName(userId, keyName);
			Logger.log(CLASS_NAME, "completeUpload: 画像ファイル名の保存が完了しました。");
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			Logger.log(CLASS_NAME, "[Error] completeUpload: " + e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			Logger.log(CLASS_NAME, "[Error] completeUpload: 画像ファイル名の保存中にエラーが発生しました。" + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
    }
	
	
	
}
