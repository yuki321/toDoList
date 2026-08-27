package com.example.todolist;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.todolist.entity.ToDo;
import com.example.todolist.repository.ToDoRepositoryIF;
import com.example.todolist.service.ToDoService;

@ExtendWith(MockitoExtension.class)
class PagerTest {

    @Mock
    private ToDoRepositoryIF toDoRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ToDoService toDoService;

    private ToDo toDo;
    

    @BeforeEach
    void setUp() {
        toDo = new ToDo();
        toDo.setId(1L);
        toDo.setUserId(1L);
        toDo.setContent("テストコメント");
        toDo.setMemo("Memo");
        toDo.setStatus(true);
        toDo.setDeadline(null);
        toDo.setPriority("1");
        
    }
    

    @Nested
    @DisplayName("タスク全件取得のテスト")
    class FindAllToDoTest {

    	@Test
        @DisplayName("全件取得が正しくリポジトリから返却されること")
        void testFindAllToDo() {

    		List<Map<String, Object>> expectedMapList = getExpectedMapList();
            when(toDoRepository.findAllToDo(userDetails)).thenReturn(expectedMapList);

            List<Map<String, Object>> actualList = toDoRepository.findAllToDo(userDetails);
            
            assertThat(actualList).isEqualTo(expectedMapList);
            verify(toDoRepository, times(1)).findAllToDo(userDetails);
        }
        
        @Test
        @DisplayName("完了済みToDoの全件取得が正しく返却されること")
        void testFindAllCompletedToDo() {

        	List<Map<String, Object>> expectedMapList = getExpectedMapList();
            when(toDoRepository.findAllCompletedToDo(userDetails)).thenReturn(expectedMapList);
            
            List<Map<String, Object>> actualList = toDoRepository.findAllCompletedToDo(userDetails);
            
            assertThat(actualList).isEqualTo(expectedMapList);
            verify(toDoRepository, times(1)).findAllCompletedToDo(userDetails);
        }
        
        private List<Map<String, Object>> getExpectedMapList() {
            List<Map<String, Object>> expectedMapList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            
        	map.put("id", 1L);
        	map.put("user_id", 1L);
        	map.put("content", "テストコメント");
        	map.put("memo", "Memo");
        	map.put("status", true);
        	map.put("deadline", null);
        	map.put("priority", "1");
        	expectedMapList.add(map);
        	
        	return expectedMapList;
        }
        
    }

    @Nested
    @DisplayName("completeTask メソッドのテスト")
    class CompleteTaskTest {
        @Test
        @DisplayName("タスク完了処理が成功した場合、trueを返すこと")
        void testCompleteTask_Success() {
            when(toDoRepository.completeTask(toDo)).thenReturn(1);

            boolean result = toDoService.completeTask(toDo);

			assertThat(result).isTrue();
            verify(toDoRepository, times(1)).completeTask(toDo);
        }

        @Test
        @DisplayName("更新対象がなく0件の場合、falseを返すこと")
        void testCompleteTask_Failure() {
            when(toDoRepository.completeTask(toDo)).thenReturn(0);

            boolean result = toDoService.completeTask(toDo);

            assertThat(result).isFalse();
            verify(toDoRepository, times(1)).completeTask(toDo);
        }
    }

    @Nested
    @DisplayName("undoCompletedTask メソッドのテスト")
    class UndoCompletedTaskTest {
        @Test
        @DisplayName("タスク完了取り消しが成功した場合、trueを返すこと")
        void testUndoCompletedTask_Success() {
            when(toDoRepository.undoCompletedTask(toDo)).thenReturn(1);

            boolean result = toDoService.undoCompletedTask(toDo);

            assertThat(result).isTrue();
            verify(toDoRepository, times(1)).undoCompletedTask(toDo);
        }

        @Test
        @DisplayName("更新対象がなく0件の場合、falseを返すこと")
        void testUndoCompletedTask_Failure() {
            when(toDoRepository.undoCompletedTask(toDo)).thenReturn(0);

            boolean result = toDoService.undoCompletedTask(toDo);

            assertThat(result).isFalse();
            verify(toDoRepository, times(1)).undoCompletedTask(toDo);
        }
    }

    @Nested
    @DisplayName("getUserInfo メソッドのテスト")
    class GetUserInfoTest {
        @Test
        @DisplayName("ユーザー情報が正しく取得できること")
        void testGetUserInfo() {
            Map<String, Object> expectedInfo = Map.of("username", "testUser");
            when(toDoRepository.getUserInfo(userDetails)).thenReturn(expectedInfo);

            Map<String, Object> actualInfo = toDoService.getUserInfo(userDetails);

            assertThat(actualInfo).isEqualTo(expectedInfo);
            verify(toDoRepository, times(1)).getUserInfo(userDetails);
        }
    }

    @Nested
    @DisplayName("insertRecord メソッドのテスト")
    class InsertRecordTest {
        @Test
        @DisplayName("ToDo作成が成功した場合、trueを返すこと")
        void testInsertRecord_Success() {
            when(toDoRepository.insertRecord(toDo)).thenReturn(1);

            boolean result = toDoService.insertRecord(toDo);

            assertThat(result).isTrue();
            verify(toDoRepository, times(1)).insertRecord(toDo);
        }

        @Test
        @DisplayName("作成失敗（0件挿入）の場合、falseを返すこと")
        void testInsertRecord_Failure() {
            when(toDoRepository.insertRecord(toDo)).thenReturn(0);

            boolean result = toDoService.insertRecord(toDo);

            assertThat(result).isFalse();
            verify(toDoRepository, times(1)).insertRecord(toDo);
        }
    }

    @Nested
    @DisplayName("updateRecord メソッドのテスト")
    class UpdateRecordTest {
        @Test
        @DisplayName("タスク編集が成功した場合、trueを返すこと")
        void testUpdateRecord_Success() {
            when(toDoRepository.updateRecord(toDo)).thenReturn(1);

            boolean result = toDoService.updateRecord(toDo);

            assertThat(result).isTrue();
            verify(toDoRepository, times(1)).updateRecord(toDo);
        }

        @Test
        @DisplayName("更新対象がなく0件の場合、falseを返すこと")
        void testUpdateRecord_Failure() {
            when(toDoRepository.updateRecord(toDo)).thenReturn(0);

            boolean result = toDoService.updateRecord(toDo);

            assertThat(result).isFalse();
            verify(toDoRepository, times(1)).updateRecord(toDo);
        }
    }

    @Nested
    @DisplayName("deleteRecord メソッドのテスト")
    class DeleteRecordTest {
        private final Long targetId = 1L;

        @Test
        @DisplayName("タスク削除が成功した場合、trueを返すこと")
        void testDeleteRecord_Success() {
            when(toDoRepository.deleteRecord(targetId)).thenReturn(1);

            boolean result = toDoService.deleteRecord(targetId);

            assertThat(result).isTrue();
            verify(toDoRepository, times(1)).deleteRecord(targetId);
        }

        @Test
        @DisplayName("削除対象がなく0件の場合、falseを返すこと")
        void testDeleteRecord_Failure() {
            when(toDoRepository.deleteRecord(targetId)).thenReturn(0);

            boolean result = toDoService.deleteRecord(targetId);

            assertThat(result).isFalse();
            verify(toDoRepository, times(1)).deleteRecord(targetId);
        }
    }
}





