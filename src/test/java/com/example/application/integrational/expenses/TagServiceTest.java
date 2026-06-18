package com.example.application.integrational.expenses;

import com.example.application.Application;
import com.example.application.data.requests.expenses.CreateTagRequest;
import com.example.application.data.requests.expenses.UpdateTagRequest;
import com.example.application.entities.User;
import com.example.application.entities.expenses.Tag;
import com.example.application.integrational.AbstractTest;
import com.example.application.services.expenses.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class TagServiceTest extends AbstractTest {

	private final TagService tagService;

	private User user;

	@Autowired
	public TagServiceTest(TagService tagService) {
		this.tagService = tagService;
	}

	@BeforeEach
	void setup() {
		this.user = createUser("user", "test@email.com");
	}

	@Test
	void saveTagSuccessfully() {
		Tag saved = createTag("Food");
		Tag found = instrumentsFacadeService.findTagById(saved.getId()).orElseThrow();

		Assertions.assertNotNull(found.getId(), "Tag ID should be generated");
		Assertions.assertEquals("Food", found.getName(), "Tag name should match");
		Assertions.assertEquals(user.getId(), found.getUser().getId(), "Tag should belong to user");
		Assertions.assertNotNull(found.getLastTimeUpdated());
		Assertions.assertNotNull(found.getTimeCreatedAt());
	}

	@Test
	void shouldNotAllowTagWithoutUser() {
		CreateTagRequest request = new CreateTagRequest("name", null);

		Assertions.assertThrows(
				NullPointerException.class,
				() -> instrumentsFacadeService.createTag(request),
				"Tag without user should fail validation"
		);
	}

	@Test
	void newlyCreatedUserShouldHaveDefaultTags() {
		CreateTagRequest request = createTagRequest("Food");

		Tag saved = instrumentsFacadeService.createTag(request);

		List<Tag> tags = tagService.findByUser(user.getId());

		Assertions.assertFalse(tags.isEmpty(), "User should have tags");
		Assertions.assertEquals(1, tags.size(), "User should have exactly one tag");
		Assertions.assertEquals(saved.getId(), tags.getFirst().getId());
	}

	@Test
	void deleteShouldRemoveTag() {
		Tag tag = instrumentsFacadeService.createTag(createTagRequest("Food"));

		tagService.delete(tag.getId());
		Assertions.assertTrue(tagService.findById(tag.getId()).isEmpty(), "Tag is still present in database");
	}

	@Test
	void deleteTagDoesNotExist() {
		Assertions.assertThrows(
				EntityNotFoundException.class,
				() -> tagService.delete(999L),
				"Deleting a non-existent tag should throw"
		);
	}

	@Test
	void singleTagModifyTest() {
		Tag originalTag = createTag("Test-Name");

		LocalDateTime timeCreated = tagService.findById(originalTag.getId())
				.orElseThrow()
				.getLastTimeUpdated();

		String newTagName = "Groceries";
		UpdateTagRequest updateRequest = new UpdateTagRequest(originalTag.getId(), newTagName);

		Tag updatedTag = instrumentsFacadeService.updateTag(updateRequest);

		LocalDateTime timeUpdated = tagService.findById(originalTag.getId())
				.orElseThrow()
				.getLastTimeUpdated();

		Assertions.assertEquals(
				newTagName,
				updatedTag.getName(),
				"Tag name was not updated"
		);

		Assertions.assertTrue(!timeCreated.equals(timeUpdated) && timeUpdated.isAfter(timeCreated),
				"lastTimeUpdated is not correct");
	}

	//<editor-fold desc="Utils">
	private Tag createTag(String name) {
		return instrumentsFacadeService.createTag(createTagRequest(name));
	}

	private CreateTagRequest createTagRequest(String name) {
		return new CreateTagRequest(name, user.getId());
	}
	//</editor-fold>
}