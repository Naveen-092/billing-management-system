package in.naveen.billingsoftware.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.naveen.billingsoftware.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long>{
	
	Optional <CategoryEntity> findByCategoryId(String categoryId);
}
