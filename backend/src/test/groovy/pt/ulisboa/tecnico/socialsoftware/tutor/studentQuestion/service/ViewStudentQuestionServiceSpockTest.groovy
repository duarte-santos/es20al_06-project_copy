package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ViewStudentQuestionServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "CourseOne"
    public static final String ACRONYM = "C1"
    public static final String ACADEMIC_TERM = "1st Term"
    public static final String FIRST_NAME = "Student Name"
    public static final String USERNAME = "Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CORRECT_CONTENT = "Correct Content"
    public static final String OPTION_INCORRECT_CONTENT = "Incorrect Content"
    public static final String JUSTIFICATION = "justification"
    public static final String URL = 'URL'

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def user
    def studentQuestionDto
    def studentQuestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
    }

    def "student views empty set of studentQuestion"() {
        when:
        def result = studentQuestionService.findStudentQuestions(course.getId())

        then: "the result is empty"
        result.size() == 0
    }

    def "student views two studentQuestion"() {
        given: "two studentQuestion"
        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        studentQuestionDto = new StudentQuestionDto(1, QUESTION_TITLE, QUESTION_CONTENT, 1, options)
        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionDto = new StudentQuestionDto(2, QUESTION_TITLE, QUESTION_CONTENT, 1, options)
        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)

        when:
        def result = studentQuestionService.findStudentQuestionsFromStudent(user.getId())
        // result contains a list of existing studentQuestions

        then: "the returned data is correct"
        result.size() == 2
        def res0 = result.get(0)
        res0.getId() != null
        res0.getTitle() == QUESTION_TITLE
        res0.getContent() == QUESTION_CONTENT
        res0.getCorrect() == 1
        res0.getOptions() == options //FIXME compare

        def res1 = result.get(1)
        res1.getId() != null
        res1.getTitle() == QUESTION_TITLE
        res1.getContent() == QUESTION_CONTENT
        res1.getCorrect() == 1
        res1.getOptions() == options //FIXME compare
    }

    def "student views studentQuestion with justification"() {
        given: "a studentQuestion with justification"
        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        studentQuestionDto = new StudentQuestionDto(1, QUESTION_TITLE, QUESTION_CONTENT, 1, options)
        studentQuestionDto.setJustification(JUSTIFICATION)
        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)

        when:
        def result = studentQuestionService.findStudentQuestionsFromStudent(user.getId())

        then: "the returned data is correct"
        result.size() == 1
        def res = result.get(0)
        res.getId() != null
        res.getTitle() == QUESTION_TITLE
        res.getContent() == QUESTION_CONTENT
        res.getCorrect() == 1
        res.getOptions() == options //FIXME compare
        res.getJustification() == JUSTIFICATION
    }

    def "student views studentQuestion with an image"() {
        given: "a studentQuestion with an image"
        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        studentQuestionDto = new StudentQuestionDto(1, QUESTION_TITLE, QUESTION_CONTENT, 1, options)
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        studentQuestionDto.setImage(image)
        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)

        when:
        def result = studentQuestionService.findStudentQuestionsFromStudent(user.getId())

        then: "the returned data is correct"
        result.size() == 1
        def res = result.get(0)
        res.getId() != null
        res.getTitle() == QUESTION_TITLE
        res.getContent() == QUESTION_CONTENT
        res.getCorrect() == 1
        res.getOptions() == options //FIXME compare
        res.getId() != null
        res.getImage().getUrl() == URL
        res.getImage().getWidth() == 20
    }

    @TestConfiguration
    static class StudentViewServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

}
