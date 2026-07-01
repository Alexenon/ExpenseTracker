package com.example.application.views.components;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.AssetWatcherDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.requests.asset_watchers.CreateAssetWatcherRequest;
import com.example.application.data.requests.asset_watchers.UpdateAssetWatcherRequest;
import com.example.application.entities.common.TransactionType;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.MoneyField;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class PriceWatchlistComponent extends Div implements HasNotifications {

	private final PortfolioDTO portfolio;
	private final AssetDTO asset;
	private final TransactionType transactionType;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final List<AssetWatcherDTO> userWatchers;
	private final Div priceLayoutContainer = new Div();

	@Autowired
	public PriceWatchlistComponent(
			PortfolioDTO portfolio,
			AssetDTO asset,
			TransactionType transactionType,
			InstrumentsFacadeService instrumentsFacadeService)
	{
		this.portfolio = portfolio;
		this.asset = asset;
		this.transactionType = transactionType;
		this.instrumentsFacadeService = instrumentsFacadeService;

		this.userWatchers = instrumentsFacadeService.getAssetWatchersByAssetAndActionType(
				portfolio.getId(),
				asset.getSymbol(),
				transactionType
		);

		add(priceLayoutContainer);
		fillComponent();
	}

	private void fillComponent() {
		if (userWatchers.isEmpty()) {
			addNewPriceLayout();
		} else {
			userWatchers.forEach(watcher ->
					priceLayoutContainer.add(new WatchlistLayout(watcher))
			);
		}
	}

	public void addNewPriceLayout() {
		priceLayoutContainer.add(new WatchlistLayout());
	}

	/**
	 * ============================
	 * WATCHLIST FORM
	 * ============================
	 */
	private class WatchlistLayout extends Div {

		private final Binder<AssetWatcherDTO> binder = new Binder<>(AssetWatcherDTO.class);

		private final Paragraph status = new Paragraph();
		private final MoneyField target = new MoneyField("Price");
		private final MoneyField targetAmount = new MoneyField("Amount in USD");
		private final Checkbox markAsCompleted = new Checkbox();
		private final Container checkboxContainer =
				new Container("centered-row", markAsCompleted, new Span("Mark as completed"));

		private final Button saveBtn = new Button("Save");
		private final Button deleteBtn = new Button("Delete");
		private final Button editBtn = new Button(LumoIcon.EDIT.create());

		private AssetWatcherDTO assetWatcher;

		private boolean isDraft;
		private boolean isEditMode;

		/**
		 * CREATE
		 */
		public WatchlistLayout() {
			this.assetWatcher = new AssetWatcherDTO();
			this.assetWatcher.setPortfolioId(portfolio.getId());
			this.assetWatcher.setAssetSymbol(asset.getSymbol());
			this.assetWatcher.setTransactionType(transactionType);
			this.isDraft = true;
			this.isEditMode = true;
			init();
		}

		/**
		 * UPDATE
		 */
		public WatchlistLayout(AssetWatcherDTO assetWatcher) {
			this.assetWatcher = assetWatcher;
			this.isDraft = false;
			this.isEditMode = false;
			init();
		}

		private void init() {
			addClassNames("section-card-wrapper", "price-watcher-wrapper");

			Container content = Container.builder("price-watcher-card-content")
					.addComponent(status)
					.addComponent(buildBody())
					.addComponent(buildFooter())
					.build();

			add(content, editBtn);

			target.setLabel(
					StringUtils.uppercaseFirstLetter(transactionType.name())
					+ " " + target.getLabel()
			);

			saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
			deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

			saveBtn.addClickListener(e -> onSave());
			deleteBtn.addClickListener(e -> removeFromParent());
			editBtn.addClickListener(e -> toggleEditMode());

			initBinder();
			updateComponentStatus();
			setEditMode(isEditMode);
		}

		private Div buildBody() {
			return new Container(
					"card-wrapper-body",
					target,
					targetAmount,
					checkboxContainer
			);
		}

		private Div buildFooter() {
			return new Container("card-wrapper-footer", saveBtn, deleteBtn);
		}

		private void initBinder() {
			binder.forField(target)
					.asRequired("Please fill this field")
					.withConverter(new StringToBigDecimalConverter(BigDecimal.ZERO, "Invalid number"))
					.withValidator(v -> v.signum() > 0, "Must be greater than 0")
					.bind(AssetWatcherDTO::getTargetPrice, AssetWatcherDTO::setTargetPrice);

			binder.forField(targetAmount)
					.asRequired("Please fill this field")
					.withConverter(new StringToBigDecimalConverter(BigDecimal.ZERO, "Invalid number"))
					.withValidator(v -> v.signum() > 0, "Must be greater than 0")
					.bind(AssetWatcherDTO::getTargetAmount, AssetWatcherDTO::setTargetAmount);

			binder.forField(markAsCompleted)
					.bind(AssetWatcherDTO::isCompleted, AssetWatcherDTO::setCompleted);

			binder.readBean(assetWatcher);
		}

		private void onSave() {
			if (!binder.writeBeanIfValid(assetWatcher)) {
				showErrorNotification("Validation failed");
				return;
			}

			if (isDraft) {
				CreateAssetWatcherRequest request = CreateAssetWatcherRequest.builder()
						.portfolioId(portfolio.getId())
						.assetSymbol(asset.getSymbol())
						.transactionType(transactionType)
						.isCompleted(assetWatcher.isCompleted())
						.targetAmount(assetWatcher.getTargetAmount())
						.targetPrice(assetWatcher.getTargetPrice())
						.build();

				assetWatcher = instrumentsFacadeService.createAssetWatcher(request);
				isDraft = false;
			} else {
				UpdateAssetWatcherRequest request = new UpdateAssetWatcherRequest(assetWatcher);
				instrumentsFacadeService.updateAssetWatcher(request);
			}

			setEditMode(false);
			updateComponentStatus();
			showSuccessfulNotification("Successfully saved");
		}

		//<editor-fold desc="UI HELPERS">
		private void toggleEditMode() {
			isEditMode = !isEditMode;
			setEditMode(isEditMode);
			binder.readBean(assetWatcher);
		}

		private void setEditMode(boolean editMode) {
			target.setReadOnly(!editMode);
			targetAmount.setReadOnly(!editMode);
			checkboxContainer.setVisible(editMode);
			saveBtn.setVisible(editMode);
			deleteBtn.setVisible(editMode);
			status.setVisible(!editMode);
			editBtn.setIcon(editMode ? LumoIcon.UNDO.create() : LumoIcon.EDIT.create());
		}

		private void updateComponentStatus() {
			status.removeClassNames("draft", "completed", "ongoing");

			String text =
					isDraft
							? "Draft"
							: markAsCompleted.getValue()
							? "Completed"
							: "Ongoing";

			status.setText(text);
			status.addClassName(text.toLowerCase());
		}
		//</editor-fold>
	}

}
