package com.example.application.views.components.core;

import com.example.application.utils.common.lang.StringUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class TagInput extends CustomField<Set<String>> {

	private final Set<String> selectedTags = new LinkedHashSet<>();

	private final ListDataProvider<String> dataProvider;

	private final ComboBox<String> input = new ComboBox<>();
	private final FlexLayout tagsContainer = new FlexLayout();
	private final FlexLayout content = new FlexLayout();

	public TagInput() {
		this(Set.of());
	}

	public TagInput(Collection<String> tags) {
		this.dataProvider = new ListDataProvider<>(new LinkedHashSet<>(tags));

		initialize();
	}

	private void initialize() {
		input.setPlaceholder("Add tag...");
		input.setWidthFull();
		input.setItems(dataProvider);

		tagsContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);

		configureCustomTags();
		configureExistingTags();

		content.addClassName("input-tag-container");
		content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
		content.add(tagsContainer, input);
		add(content);
	}

	private void configureCustomTags() {
		input.setAllowCustomValue(true);

		input.addCustomValueSetListener(event -> {
			String tag = event.getDetail();

			addTag(tag);

			if (!dataProvider.getItems().contains(tag)) {
				dataProvider.getItems().add(tag);
				dataProvider.refreshAll();
			}

			input.clear();
		});
	}

	private void configureExistingTags() {
		input.addValueChangeListener(event -> {
			String tag = event.getValue();

			if (StringUtils.isNotBlank(tag)) {
				addTag(tag);
				input.clear();
			}
		});
	}

	private void addTag(String tag) {
		if (StringUtils.isBlank(tag)) {
			return;
		}

		tag = tag.trim();

		if (!selectedTags.add(tag)) {
			return;
		}

		tagsContainer.add(createBadge(tag));

		updateValue();
	}

	private void removeTag(String tag) {
		selectedTags.remove(tag);

		tagsContainer.getChildren()
				.filter(component ->
						component.getId()
								.map(id -> id.equals("tag-" + tag))
								.orElse(false))
				.findFirst()
				.ifPresent(tagsContainer::remove);

		updateValue();
	}

	private Div createBadge(String tag) {
		Span label = new Span(tag);

		Button removeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), e -> removeTag(tag));
		removeButton.addClassName("remove-tag-btn");
		// Removes the margin from the shadow-root of the vaadin-button-container
		removeButton.getElement()
				.executeJs("this.shadowRoot.querySelector('vaadin-button-container').style.height = 20px;");
//		removeButton.getStyle()
//				.set("min-width", "24px")
//				.set("padding", "0");

		Div badge = new Div(label, removeButton);
		badge.setId("tag-" + tag);
		badge.setClassName("tag-badge");
//		badge.setSpacing(false);
//		badge.getStyle()
//				.set("align-items", "center")
//				.set("border-radius", "999px")
//				.set("padding", "4px 8px")
//				.set("margin", "2px")
//				.set("background", "var(--lumo-contrast-10pct)");

		return badge;
	}

	public void setItems(Collection<String> tags) {
		dataProvider.getItems().clear();
		dataProvider.getItems().addAll(tags);

		dataProvider.refreshAll();
	}

	public Set<String> getItems() {
		return Collections.unmodifiableSet(selectedTags);
	}

	@Override
	protected Set<String> generateModelValue() {
		return Set.copyOf(selectedTags);
	}

	@Override
	protected void setPresentationValue(Set<String> value) {
		selectedTags.clear();
		tagsContainer.removeAll();

		if (value == null) {
			return;
		}

		value.forEach(this::addTag);
	}
}