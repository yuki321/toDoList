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

import com.example.todolist.entity.Pager;
import com.example.todolist.entity.ToDo;
import com.example.todolist.repository.ToDoRepositoryIF;
import com.example.todolist.service.ToDoService;


class ToDoServiceTest {


    private Pager pager;

    @BeforeEach
    void setUp() {
    	pager = new Pager();
        
    }
    

    @Nested
    @DisplayName("getTopIndex メソッドのテスト")
    class getTopIndexTest {

    	@Test
        @DisplayName("page=0　の場合")
        void getTopIndex_page_zero() {

        	final int currnetPage = 0;
        	final int dataCountPerPage = 10;
        	
        	
        	int actualResult = pager.getTopIndex(currnetPage, dataCountPerPage);

    		assertThat(actualResult).isEqualTo(1);
        }
        
        @Test
        @DisplayName("page=1~　の場合")
        void getTopIndex_page_moreThan_one() {

        	final int currnetPage1 = 1;
        	final int currentPage10 = 10;
        	final int dataCountPerPage = 10;
        	
        	int actualResult1 = pager.getTopIndex(currnetPage1, dataCountPerPage);
        	int actualResult2 = pager.getTopIndex(currentPage10, dataCountPerPage);

    		assertThat(actualResult1).isEqualTo(11);
    		assertThat(actualResult2).isEqualTo(101);
        }
        
        @Test
        @DisplayName("pageが負の数　の場合")
        void getTopIndex_page_negative() {

        	final int currnetPage1 = -1;
        	final int currentPage10 = -10;
        	final int dataCountPerPage = 10;
        	
        	int actualResult1 = pager.getTopIndex(currnetPage1, dataCountPerPage);
        	int actualResult2 = pager.getTopIndex(currentPage10, dataCountPerPage);

    		assertThat(actualResult1).isEqualTo(1);
    		assertThat(actualResult2).isEqualTo(1);
        }
        
    }

    @Nested
    @DisplayName("getLastIndex メソッドのテスト")
    class getLastIndexTest {

    	@Test
        @DisplayName("page=0　の場合")
        void getLastIndex_page_zero() {

        	final int currnetPage = 0;
        	final int dataCountPerPage = 10;
        	
        	
        	int actualResult = pager.getLastIndex(currnetPage, dataCountPerPage);

    		assertThat(actualResult).isEqualTo(10);
        }
        
        @Test
        @DisplayName("page=1~　の場合")
        void getLastIndex_page_moreThan_one() {

        	final int currnetPage1 = 1;
        	final int currentPage10 = 10;
        	final int dataCountPerPage = 10;
        	
        	int actualResult1 = pager.getLastIndex(currnetPage1, dataCountPerPage);
        	int actualResult2 = pager.getLastIndex(currentPage10, dataCountPerPage);

    		assertThat(actualResult1).isEqualTo(20);
    		assertThat(actualResult2).isEqualTo(110);
        }
        
        @Test
        @DisplayName("page=1~　の場合")
        void getLastIndex_page_negative() {

        	final int currnetPage1 = -1;
        	final int currentPage10 = -10;
        	final int dataCountPerPage = 10;
        	
        	int actualResult1 = pager.getLastIndex(currnetPage1, dataCountPerPage);
        	int actualResult2 = pager.getLastIndex(currentPage10, dataCountPerPage);

    		assertThat(actualResult1).isEqualTo(10);
    		assertThat(actualResult2).isEqualTo(10);
        }
        
    }


}





