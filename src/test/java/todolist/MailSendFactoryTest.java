package todolist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import todolist.entity.MailSend;
import todolist.entity.MailSendFactory;
import todolist.entity.PasswordResetMail;
import todolist.entity.UserCreateMail;
import todolist.service.MailService;


@ExtendWith(MockitoExtension.class)
public class MailSendFactoryTest {
	
	@Mock
	private MailService mailService;
	
	@Mock
	private PasswordResetMail passwordResetMail;

	@Nested
	@DisplayName("getMailSendKind メソッドのテスト")
	class getMailSendKindTest {
	    @Test
	    @DisplayName("メール送信処理の種類を取得すること")
	    void getMailSendKind_Success() {

	    	MailSend expected1 = MailSendFactory.getMailSendKind("PW_RESET", mailService);
	    	MailSend actual1 = new PasswordResetMail(mailService);
	    	
	    	MailSend expected2 = MailSendFactory.getMailSendKind("USER_CREATE", mailService);
	    	MailSend actual2 = new UserCreateMail(mailService);
	    	
	    	assertEquals(actual1.getClass(), expected1.getClass());
	    	assertEquals(actual2.getClass(), expected2.getClass());
	    }

	    @Test
	    @DisplayName("mailSendKindがnullの場合、nullを返すこと")
	    void getMailSendKind_Kind_null() {

	    	MailSend expected1 = MailSendFactory.getMailSendKind(null, mailService);
	    	
	    	assertNull(expected1);
	    }
	    	    
	}

}



