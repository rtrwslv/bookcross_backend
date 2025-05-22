package ru.itport.bookcross.view.location

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
import ru.itport.bookcross.entity.Location
import ru.itport.bookcross.view.main.MainView

@Route(value = "locations", layout = MainView::class)
@ViewController(id = "Location.list")
@ViewDescriptor(path = "location-list-view.xml")
@LookupComponent("locationsDataGrid")
@DialogMode(width = "64em")
class LocationListView : StandardListView<Location>() {

    @ViewComponent
    private lateinit var dataContext: DataContext

    @ViewComponent
    private lateinit var locationsDc: CollectionContainer<Location>

    @ViewComponent
    private lateinit var locationDc: InstanceContainer<Location>

    @ViewComponent
    private lateinit var locationDl: InstanceLoader<Location>

    @ViewComponent
    private lateinit var listLayout: VerticalLayout

    @ViewComponent
    private lateinit var locationsDataGrid: DataGrid<Location>

    @ViewComponent
    private lateinit var form: FormLayout

    @ViewComponent
    private lateinit var detailActions: HorizontalLayout

    @Subscribe
    fun onInit(event: InitEvent) {
        locationsDataGrid.getActions().forEach { action ->
            if (action is SecuredBaseAction) {
                action.addEnabledRule { listLayout.isEnabled }
            }
        }
    }

    @Subscribe
    fun onBeforeShow(event: BeforeShowEvent) {
        updateControls(false)
    }

    @Subscribe("locationsDataGrid.create")
    fun onLocationsDataGridCreate(event: ActionPerformedEvent) {
        dataContext.clear()
        val entity: Location = dataContext.create(Location::class.java)
        locationDc.item = entity
        updateControls(true)
    }

    @Subscribe("locationsDataGrid.edit")
    fun onLocationsDataGridEdit(event: ActionPerformedEvent) {
        updateControls(true)
    }

    @Subscribe("saveButton")
    fun onSaveButtonClick(event: ClickEvent<JmixButton>) {
        val item = locationDc.item
        val validationErrors = validateView(item)
        if (!validationErrors.isEmpty) {
            val viewValidation = getViewValidation()
            viewValidation.showValidationErrors(validationErrors)
            viewValidation.focusProblemComponent(validationErrors)
            return
        }
        dataContext.save()
        locationsDc.replaceItem(item)
        updateControls(false)
    }

    @Subscribe("cancelButton")
    fun onCancelButtonClick(event: ClickEvent<JmixButton>) {
        dataContext.clear()
        locationDc.setItem(null)
        locationDl.load()
        updateControls(false)
    }

    @Subscribe(id = "locationsDc", target = Target.DATA_CONTAINER)
    fun onLocationsDcItemChange(event: InstanceContainer.ItemChangeEvent<Location>) {
        val entity: Location? = event.item
        dataContext.clear()
        if (entity != null) {
            locationDl.entityId = entity.id
            locationDl.load()
        } else {
            locationDl.entityId = null
            locationDc.setItem(null)
        }
        updateControls(false)
    }

    private fun validateView(entity: Location): ValidationErrors {
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
        locationsDataGrid.getActions().forEach(Action::refreshState);
    }

    private fun getViewValidation(): ViewValidation {
        return applicationContext.getBean(ViewValidation::class.java)
    }
}