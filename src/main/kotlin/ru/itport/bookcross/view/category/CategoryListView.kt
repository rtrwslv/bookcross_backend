package ru.itport.bookcross.view.category

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
import ru.itport.bookcross.entity.Category
import ru.itport.bookcross.view.main.MainView

@Route(value = "categories", layout = MainView::class)
@ViewController(id = "Category.list")
@ViewDescriptor(path = "category-list-view.xml")
@LookupComponent("categoriesDataGrid")
@DialogMode(width = "64em")
class CategoryListView : StandardListView<Category>() {

    @ViewComponent
    private lateinit var dataContext: DataContext

    @ViewComponent
    private lateinit var categoriesDc: CollectionContainer<Category>

    @ViewComponent
    private lateinit var categoryDc: InstanceContainer<Category>

    @ViewComponent
    private lateinit var categoryDl: InstanceLoader<Category>

    @ViewComponent
    private lateinit var listLayout: VerticalLayout

    @ViewComponent
    private lateinit var categoriesDataGrid: DataGrid<Category>

    @ViewComponent
    private lateinit var form: FormLayout

    @ViewComponent
    private lateinit var detailActions: HorizontalLayout

    @Subscribe
    fun onInit(event: InitEvent) {
        categoriesDataGrid.getActions().forEach { action ->
            if (action is SecuredBaseAction) {
                action.addEnabledRule { listLayout.isEnabled }
            }
        }
    }

    @Subscribe
    fun onBeforeShow(event: BeforeShowEvent) {
        updateControls(false)
    }

    @Subscribe("categoriesDataGrid.create")
    fun onCategoriesDataGridCreate(event: ActionPerformedEvent) {
        dataContext.clear()
        val entity: Category = dataContext.create(Category::class.java)
        categoryDc.item = entity
        updateControls(true)
    }

    @Subscribe("categoriesDataGrid.edit")
    fun onCategoriesDataGridEdit(event: ActionPerformedEvent) {
        updateControls(true)
    }

    @Subscribe("saveButton")
    fun onSaveButtonClick(event: ClickEvent<JmixButton>) {
        val item = categoryDc.item
        val validationErrors = validateView(item)
        if (!validationErrors.isEmpty) {
            val viewValidation = getViewValidation()
            viewValidation.showValidationErrors(validationErrors)
            viewValidation.focusProblemComponent(validationErrors)
            return
        }
        dataContext.save()
        categoriesDc.replaceItem(item)
        updateControls(false)
    }

    @Subscribe("cancelButton")
    fun onCancelButtonClick(event: ClickEvent<JmixButton>) {
        dataContext.clear()
        categoryDc.setItem(null)
        categoryDl.load()
        updateControls(false)
    }

    @Subscribe(id = "categoriesDc", target = Target.DATA_CONTAINER)
    fun onCategoriesDcItemChange(event: InstanceContainer.ItemChangeEvent<Category>) {
        val entity: Category? = event.item
        dataContext.clear()
        if (entity != null) {
            categoryDl.entityId = entity.id
            categoryDl.load()
        } else {
            categoryDl.entityId = null
            categoryDc.setItem(null)
        }
        updateControls(false)
    }

    private fun validateView(entity: Category): ValidationErrors {
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
        categoriesDataGrid.getActions().forEach(Action::refreshState);
    }

    private fun getViewValidation(): ViewValidation {
        return applicationContext.getBean(ViewValidation::class.java)
    }
}