package ru.itport.bookcross.controller

import org.springframework.web.bind.annotation.*
import ru.itport.bookcross.models.BaseResponse
import ru.itport.bookcross.models.CategoryDTO
import ru.itport.bookcross.services.CategoryService
import ru.itport.bookcross.utils.ControllerUtils.Companion.serviceCall
import java.util.*

@RestController
@CrossOrigin
@RequestMapping("custom/api/v1/category")
class CategoryController(
    val categoryService: CategoryService
) {

    @GetMapping("/all")
    fun getAllCategories(): BaseResponse<List<CategoryDTO>> {
        return serviceCall { categoryService.getAllCategories() }
    }


    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: UUID): BaseResponse<CategoryDTO> {
        return serviceCall { categoryService.getCategoryById(id) }
    }
}