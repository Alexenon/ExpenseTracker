package com.example.application.integrational.expenses;

import com.example.application.Application;
import com.example.application.data.dtos.expense.CategoryDTO;
import com.example.application.data.requests.expenses.category.CreateCategoryRequest;
import com.example.application.data.requests.expenses.category.UpdateCategoryRequest;
import com.example.application.entities.User;
import com.example.application.entities.expenses.Category;
import com.example.application.integrational.AbstractTest;
import com.example.application.services.UserService;
import com.example.application.services.expenses.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class CategoryServiceTest extends AbstractTest {

	private final UserService userService;
	private final CategoryService categoryService;

	private User user;

	@Autowired
	public CategoryServiceTest(UserService userService,
							   CategoryService categoryService)
	{
		this.userService = userService;
		this.categoryService = categoryService;
	}

	@BeforeEach
	void setup() {
		this.user = createUser("user", "test@email.com");
	}

	@AfterEach
	void cleanup() {
		categoryRepository.deleteAll();
		userRepository.deleteAll();

		Assertions.assertTrue(
				userService.findById(user.getId()).isEmpty(),
				"User was not deleted"
		);

		Assertions.assertTrue(
				categoryService.findByUser(user.getId()).isEmpty(),
				"Categories were not deleted"
		);
	}

	@Test
	void saveCategorySuccessfully() {
		CreateCategoryRequest request = createCategoryRequest("Food");
		CategoryDTO saved = instrumentsFacadeService.createCategory(request);

		Assertions.assertNotNull(saved.getId(), "Category ID should be generated");
		Assertions.assertEquals("Food", saved.getName(), "Category name should match");
		Assertions.assertEquals("DEFAULT_ICON_NAME", saved.getIconName(), "Icon name should match");
		Assertions.assertEquals(user.getId(), saved.getUserId(), "Category should belong to user");
	}

	@Test
	void shouldNotAllowCategoryWithoutUser() {
		CreateCategoryRequest request = CreateCategoryRequest.builder()
				.name("Food")
				.iconName("restaurant")
				.userId(null)
				.build();

		Assertions.assertThrows(
				NullPointerException.class,
				() -> instrumentsFacadeService.createCategory(request),
				"Category without user should fail validation"
		);
	}

	@Test
	void userShouldHaveCreatedCategory() {
		CategoryDTO category = createCategory("Food");

		Optional<Category> createdCategory = categoryService.findByNameAndUser("Food", user.getId());
		Assertions.assertTrue(createdCategory.isPresent(), "Cannot found created category");
	}

	@Test
	void deleteShouldRemoveCategory() {
		CategoryDTO category = createCategory("Transport");

		categoryService.delete(category.getId());

		Assertions.assertTrue(categoryService.findById(category.getId()).isEmpty(),
				"Category is still present in database after deletion");
	}

	@Test
	void deleteCategoryDoesNotExist() {
		Assertions.assertThrows(
				EntityNotFoundException.class,
				() -> categoryService.delete(999L),
				"Deleting a non-existent category should throw"
		);
	}

	@Test
	void categoryUpdateTest() {
		CategoryDTO originalCategory = createCategory("#Healthcare");

		// TODO: Add cleaner way to do this -> builder I guess
		UpdateCategoryRequest updateRequest = new UpdateCategoryRequest(
				originalCategory.getId(),
				"#Groceries",
				"shopping_cart"
		);

		CategoryDTO updatedCategory = instrumentsFacadeService.updateCategory(updateRequest);
		Assertions.assertEquals("#Groceries", updatedCategory.getName(), "Category name was not updated");
		Assertions.assertEquals("shopping_cart", updatedCategory.getIconName(), "Icon name was not updated");
	}

	//<editor-fold desc="Utils">
	private CategoryDTO createCategory(String name) {
		return instrumentsFacadeService.createCategory(createCategoryRequest(name));
	}

	private CreateCategoryRequest createCategoryRequest(String name) {
		return CreateCategoryRequest.builder()
				.name(name)
				.iconName("DEFAULT_ICON_NAME")
				.userId(user.getId())
				.build();
	}

	//</editor-fold>
}