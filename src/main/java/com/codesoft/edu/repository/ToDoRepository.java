package com.codesoft.edu.repository;

import com.codesoft.edu.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    //Task 18
//    @Query(value = "select * " +
//                   "from todos\n" +
//                   "    where owner_id = :userId\n" +
//                   "union\n" +
//                   "select * " +
//                   "from todos t inner join todo_collaborator tc\n" +
//                   "    on t.id = tc.todo_id and tc." +
//                   "collaborator_id = :userId", nativeQuery = true)

    //Task 17
//    @Query(value = "select id, title, created_at, owner_id from todos where owner_id = ?1 union " +
//            "select id, title, created_at, owner_id from todos inner join todo_collaborator on id = todo_id and " +
//            "collaborator_id = ?1", nativeQuery = true)

    //task 12
    @Query("SELECT t FROM ToDo t WHERE t.owner.id = :userId")
    List<ToDo> getByUserId(long userId);
}
