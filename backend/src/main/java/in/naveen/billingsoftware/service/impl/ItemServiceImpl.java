package in.naveen.billingsoftware.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import in.naveen.billingsoftware.entity.CategoryEntity;
import in.naveen.billingsoftware.entity.ItemEntity;
import in.naveen.billingsoftware.io.ItemRequest;
import in.naveen.billingsoftware.io.ItemResponse;
import in.naveen.billingsoftware.repository.CategoryRepository;
import in.naveen.billingsoftware.repository.ItemRepository;
import in.naveen.billingsoftware.service.FileUploadService;
import in.naveen.billingsoftware.service.ItemService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final FileUploadService fileUploadService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) throws IOException {

        // Upload image to Cloudinary
        String imgUrl = fileUploadService.uploadFile(file);

        // Convert request to entity
        ItemEntity newItem = convertToEntity(request);

        // Find category
        CategoryEntity existingCategory =
                categoryRepository.findByCategoryId(request.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: " +
                                        request.getCategoryId()
                                )
                        );

        // Set category and image URL
        newItem.setCategory(existingCategory);
        newItem.setImgUrl(imgUrl);

        // Save item
        newItem = itemRepository.save(newItem);

        return convertToResponse(newItem);
    }

    private ItemResponse convertToResponse(ItemEntity newItem) {

        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImgUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    private ItemEntity convertToEntity(ItemRequest request) {

        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    @Override
    public List<ItemResponse> fetchItems() {

        return itemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(String itemId) {

        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item not found: " + itemId
                        )
                );

        try {

            // Delete image from Cloudinary
            fileUploadService.deleteFile(
                    existingItem.getImgUrl()
            );

            // Delete item from database
            itemRepository.delete(existingItem);

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to delete the image"
            );
        }
    }
}