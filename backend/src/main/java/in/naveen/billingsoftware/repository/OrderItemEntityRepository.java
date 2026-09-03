package in.naveen.billingsoftware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.naveen.billingsoftware.entity.OrderItemEntity;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, Long> {
}
