package ru.itport.bookcross.view.telegramchat

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.HasValueAndElement
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import io.jmix.core.validation.group.UiCrossFieldChecks
import io.jmix.flowui.action.SecuredBaseAction
import io.jmix.flowui.component.UiComponentUtils
import io.jmix.flowui.component.grid.DataGrid
import io.jmix.flowui.component.validation.ValidationErrors
import io.jmix.flowui.kit.action.Action
import io.jmix.flowui.kit.action.ActionPerformedEvent
import io.jmix.flowui.kit.component.button.JmixButton
import io.jmix.flowui.model.CollectionContainer
import io.jmix.flowui.model.DataContext
import io.jmix.flowui.model.InstanceContainer
import io.jmix.flowui.model.InstanceLoader
import io.jmix.flowui.view.*
import io.jmix.flowui.view.Target
import ru.itport.bookcross.entity.TelegramChat
import ru.itport.bookcross.view.main.MainView

@Route(value = "telegramChats", layout = MainView::class)
@ViewController(id = "TelegramChat.list")
@ViewDescriptor(path = "telegram-chat-list-view.xml")
@LookupComponent("telegramChatsDataGrid")
@DialogMode(width = "64em")
class TelegramChatListView : StandardListView<TelegramChat>() {

    @ViewComponent
    private lateinit var dataContext: DataContext

    @ViewComponent
    private lateinit var telegramChatsDc: CollectionContainer<TelegramChat>

    @ViewComponent
    private lateinit var telegramChatDc: InstanceContainer<TelegramChat>

    @ViewComponent
    private lateinit var telegramChatDl: InstanceLoader<TelegramChat>

    @ViewComponent
    private lateinit var listLayout: VerticalLayout

    @ViewComponent
    private lateinit var telegramChatsDataGrid: DataGrid<TelegramChat>

    @ViewComponent
    private lateinit var form: FormLayout

    @ViewComponent
    private lateinit var detailActions: HorizontalLayout

    @Subscribe
    fun onInit(event: InitEvent) {
        telegramChatsDataGrid.getActions().forEach { action ->
            if (action is SecuredBaseAction) {
                action.addEnabledRule { listLayout.isEnabled }
            }
        }
    }

    @Subscribe
    fun onBeforeShow(event: BeforeShowEvent) {
        updateControls(false)
    }

    @Subscribe("telegramChatsDataGrid.create")
    fun onTelegramChatsDataGridCreate(event: ActionPerformedEvent) {
        dataContext.clear()
        val entity: TelegramChat = dataContext.create(TelegramChat::class.java)
        telegramChatDc.item = entity
        updateControls(true)
    }

    @Subscribe("telegramChatsDataGrid.edit")
    fun onTelegramChatsDataGridEdit(event: ActionPerformedEvent) {
        updateControls(true)
    }

    @Subscribe("saveButton")
    fun onSaveButtonClick(event: ClickEvent<JmixButton>) {
        val item = telegramChatDc.item
        val validationErrors = validateView(item)
        if (!validationErrors.isEmpty) {
            val viewValidation = getViewValidation()
            viewValidation.showValidationErrors(validationErrors)
            viewValidation.focusProblemComponent(validationErrors)
            return
        }
        dataContext.save()
        telegramChatsDc.replaceItem(item)
        updateControls(false)
    }

    @Subscribe("cancelButton")
    fun onCancelButtonClick(event: ClickEvent<JmixButton>) {
        dataContext.clear()
        telegramChatDc.setItem(null)
        telegramChatDl.load()
        updateControls(false)
    }

    @Subscribe(id = "telegramChatsDc", target = Target.DATA_CONTAINER)
    fun onTelegramChatsDcItemChange(event: InstanceContainer.ItemChangeEvent<TelegramChat>) {
        val entity: TelegramChat? = event.item
        dataContext.clear()
        if (entity != null) {
            telegramChatDl.entityId = entity.id
            telegramChatDl.load()
        } else {
            telegramChatDl.entityId = null
            telegramChatDc.setItem(null)
        }
        updateControls(false)
    }

    private fun validateView(entity: TelegramChat): ValidationErrors {
        val viewValidation = getViewValidation()
        val validationErrors = viewValidation.validateUiComponents(form)
        if (!validationErrors.isEmpty) {
            return validationErrors
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks::class.java, entity))
        return validationErrors
    }

    private fun updateControls(editing: Boolean) {
        UiComponentUtils.getComponents(form).forEach { component ->
            if (component is HasValueAndElement<*, *>) {
                component.isReadOnly = !editing
            }
        }
        detailActions.isVisible = editing
        listLayout.isEnabled = !editing
        telegramChatsDataGrid.getActions().forEach(Action::refreshState);
    }

    private fun getViewValidation(): ViewValidation {
        return applicationContext.getBean(ViewValidation::class.java)
    }
}