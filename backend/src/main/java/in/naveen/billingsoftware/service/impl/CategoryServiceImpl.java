package in.naveen.billingsoftware.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.naveen.billingsoftware.entity.CategoryEntity;
import in.naveen.billingsoftware.io.CategoryRequest;
import in.naveen.billingsoftware.io.CategoryResponse;
import in.naveen.billingsoftware.repository.CategoryRepository;
import in.naveen.billingsoftware.repository.ItemRepository;
import in.naveen.billingsoftware.service.CategoryService;
import in.naveen.billingsoftware.service.FileUploadService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;

    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) throws IOException {

        // Upload category image to Cloudinary
        String imgUrl = fileUploadService.uploadFile(file);

        // Create category entity
        CategoryEntity newCategory = convertToEntity(request);

        // Store Cloudinary URL in database
        newCategory.setImgUrl(imgUrl);

        newCategory = categoryRepository.save(newCategory);

        return convertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {

        CategoryEntity existingCategory = categoryRepository
                .findByCategoryId(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found: " + categoryId)
                );

        // Delete category image from Cloudinary
        if (existingCategory.getImgUrl() != null &&
                !existingCategory.getImgUrl().isBlank()) {

            fileUploadService.deleteFile(existingCategory.getImgUrl());
        }

        // Delete category from database
        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {

        Integer itemsCount =
                itemRepository.countByCategoryId(newCategory.getId());

        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {

        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}