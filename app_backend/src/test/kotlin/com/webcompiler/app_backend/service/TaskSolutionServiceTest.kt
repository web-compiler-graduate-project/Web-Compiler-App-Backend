package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.*
import com.webcompiler.app_backend.repository.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class TaskSolutionServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var taskSolutionRepository: TaskSolutionRepository
    private lateinit var target: TaskSolutionService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        taskSolutionRepository = mock(TaskSolutionRepository::class.java)
        target = TaskSolutionService(userRepository, taskSolutionRepository)
    }

    @Test
    fun `should submit solution successfully`() {
        val username = "user"
        val task = Task(id = 1L, title = "Task")
        val compilationResult = CompilationResult(id = 1L)
        val user = AppUser(
            id = 1L,
            name = username,
            tasks = mutableListOf(task),
            compilationResults = mutableListOf(compilationResult),
            taskSolutions = mutableListOf()
        )

        `when`(userRepository.findByName(username)).thenReturn(user)

        target.submitSolution(username)

        verify(taskSolutionRepository, times(1)).save(any())
        assertEquals(1, user.taskSolutions.size)
        assertEquals(task, user.taskSolutions[0].task)
        assertEquals(compilationResult, user.taskSolutions[0].compilationResult)
    }

    @Test
    fun `should throw exception if user not found on submit`() {
        val username = "nonExistentUser"

        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.submitSolution(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User not found", exception.reason)
    }

    @Test
    fun `should throw exception if no assigned task on submit`() {
        val username = "user"
        val user = AppUser(id = 1L, name = username, tasks = mutableListOf(), compilationResults = mutableListOf())

        `when`(userRepository.findByName(username)).thenReturn(user)

        val exception = assertFailsWith<ResponseStatusException> {
            target.submitSolution(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("No assigned task found for given user.", exception.reason)
    }

    @Test
    fun `should throw exception if no compilation result on submit`() {
        val username = "user"
        val task = Task(id = 1L, title = "Task")
        val user = AppUser(id = 1L, name = username, tasks = mutableListOf(task), compilationResults = mutableListOf())

        `when`(userRepository.findByName(username)).thenReturn(user)

        val exception = assertFailsWith<ResponseStatusException> {
            target.submitSolution(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("No assigned compilation result found for given user registered in task.", exception.reason)
    }

    @Test
    fun `should grade solution successfully`() {
        val solutionId = 1L
        val grade = 5
        val comments = "Good job!"
        val solution = TaskSolution(id = solutionId)

        `when`(taskSolutionRepository.findById(solutionId)).thenReturn(Optional.of(solution))

        target.gradeSolution(solutionId, grade, comments)

        verify(taskSolutionRepository, times(1)).save(solution.copy(grade = grade, comments = comments))
    }

    @Test
    fun `should throw exception if solution not found on grade`() {
        val solutionId = 1L
        val grade = 5
        val comments = "Good job!"

        `when`(taskSolutionRepository.findById(solutionId)).thenReturn(Optional.empty())

        val exception = assertFailsWith<ResponseStatusException> {
            target.gradeSolution(solutionId, grade, comments)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Task solution with id $solutionId not found.", exception.reason)
    }

    @Test
    fun `should get all user task solutions`() {
        val username = "user"
        val taskSolution = TaskSolution(id = 1L)
        val user = AppUser(id = 1L, name = username, taskSolutions = mutableListOf(taskSolution))

        `when`(userRepository.findByName(username)).thenReturn(user)

        val solutions = target.getAllUserTaskSolutions(username)

        assertNotNull(solutions)
        assertEquals(1, solutions.size)
        assertEquals(taskSolution, solutions[0])
    }

    @Test
    fun `should throw exception if user not found on get all solutions`() {
        val username = "nonExistentUser"

        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.getAllUserTaskSolutions(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User not found", exception.reason)
    }
}
