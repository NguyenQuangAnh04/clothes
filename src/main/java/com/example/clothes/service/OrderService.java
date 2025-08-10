package com.example.clothes.service;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.dto.OrderItemDTO;
import com.example.clothes.enums.OrderStatus;
import com.example.clothes.mapper.OrderMapperDTO;
import com.example.clothes.model.*;
import com.example.clothes.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderMapperDTO orderMapperDTO;

    @Override
    public Page<OrderDTO> findAll(OrderStatus status, String fullName, Long orderId, String phone, PageRequest pageRequest) {
        return orderRepository.findAllByStatusAndIdAndFullName(status, orderId, fullName, phone, pageRequest).map(item -> {
            return orderMapperDTO.mapOrderToOrderDTO(item);
        });
    }

    @Transactional
    @Override
    public Order createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(currentUserService.getCurrentUser().getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        Order order = Order.builder()
                .phone(orderDTO.getPhone())
                .email(orderDTO.getEmail())
                .customerName(orderDTO.getFirstName() + " " + orderDTO.getLastName())
                .create_at(LocalDateTime.now())
                .paymentMethod(orderDTO.getPaymentMethod())
                .totalAmount(orderDTO.getSubtotal())
                .address(orderDTO.getAddress())
                .orderStatus(OrderStatus.PENDING)
                .user(user)
                .build();
        orderRepository.save(order);

        if (orderDTO.getOrderItemDTOS() != null) {
            for (OrderItemDTO orderItemDTO : orderDTO.getOrderItemDTOS()) {
                Product product = productRepository.findById(orderItemDTO.getProductId()).orElseThrow(null);
                OrderItem orderItem = OrderItem.builder()
                        .size(orderItemDTO.getSize())
                        .quantity(orderItemDTO.getQuantity())
                        .subtotal(BigDecimal.valueOf((orderDTO.getSubtotal())))
                        .color(orderItemDTO.getColor())
                        .order(order)
                        .product(product)
                        .pricePerUnit(BigDecimal.valueOf(orderItemDTO.getPrice()))
                        .build();
                orderItemRepository.save(orderItem);
            }
        } else {
            Cart cart = cartRepository.findById(orderDTO.getCartId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giỏ hàng"));
            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();

                Variant variant = variantRepository.findByProductAndColorAndSize(
                        product, cartItem.getColor(), cartItem.getSize()
                ).orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản sản phẩm"));

                if (variant.getQuantity() < cartItem.getQuantity()) {
                    throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm " + product.getProductName());
                }

                variant.setQuantity(variant.getQuantity() - cartItem.getQuantity());
                variantRepository.save(variant);

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(cartItem.getQuantity())
                        .color(cartItem.getColor())
                        .size(cartItem.getSize())
                        .pricePerUnit(BigDecimal.valueOf(cartItem.getPrice()))
                        .subtotal(cartItem.getSubtotal())
                        .build();
                orderItemRepository.save(orderItem);

            }
            cartItemRepository.deleteByCartId(orderDTO.getCartId());
            cartRepository.deleteById(cart.getId());
        }
        return order;
    }


    @Override
    public List<OrderDTO> findOrders() {
        User user = userRepository.findById(currentUserService.getCurrentUser().getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        List<Order> orders = orderRepository.findAllUser(user);
        if (orders.isEmpty()) {
            throw new RuntimeException("Không có đơn hàng nào");
        }
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order : orders) {
            orderDTOList.add(orderMapperDTO.mapOrderToOrderDTO(order));
        }
        return orderDTOList;
    }

    @Override
    public OrderDTO findOrderDetail(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Order order = orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setFullName(order.getCustomerName());
        dto.setEmail(order.getEmail());
        dto.setSubtotal(order.getTotalAmount());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreate_at(order.getOrderDate());
        dto.setStatus(order.getOrderStatus().name());
        dto.setNote(order.getNote());
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setId(item.getOrderItemId());
            Variant variant = variantRepository.findByProductAndColorAndSize(item.getProduct(), item.getColor(), item.getSize())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy product"));
            orderItemDTO.setProductName(item.getProduct().getProductName());
            orderItemDTO.setQuantity(item.getQuantity());
            orderItemDTO.setProductId(item.getProduct().getId());
            orderItemDTO.setSize(item.getSize());
            orderItemDTO.setPrice(item.getProduct().getPrice());
            orderItemDTO.setColor(item.getColor());
            orderItemDTO.setTotalAmount(item.getSubtotal());
            orderItemDTOList.add(orderItemDTO);
        }
        return dto;
    }

    @Override
    public void cancelled(Long orderId) {
        User user = userRepository.findById(currentUserService.getCurrentUser().getUserId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        Optional<Order> order = orderRepository.findByOrderIdAndUser(orderId, user);
        if (!order.isPresent()) throw new EntityNotFoundException("Không tìm thấy đơn hàng");
        order.get().setOrderStatus(OrderStatus.CANCELLED);
        order.get().setOrderDate(LocalDateTime.now());
        orderRepository.save(order.get());
    }

    public ByteArrayInputStream exportInvoicePdf(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            String FONT = "src/main/resources/fonts/NotoSans-VariableFont_wdth,wght.ttf";
            BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12, Font.NORMAL);
            Font fontTitle = new Font(baseFont, 16, Font.BOLD);
            Paragraph title = new Paragraph("Hóa đơn #" + order.getOrderId(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            document.add(new Paragraph("Khách hàng: " + order.getCustomerName(), font));
            document.add(new Paragraph("Ngày đặt hàng: " + order.getOrderDate(), font));
            document.add(Chunk.NEWLINE);
            for (OrderItem item : order.getOrderItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
                document.add(new Paragraph("- " + product.getProductName() + " - Giá: " + product.getPrice(), font));
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("-Màu:" + item.getColor() + ",Size:" + item.getSize() + ", Số lượng:" + item.getQuantity(), font));
                document.add(Chunk.NEWLINE);
            }
            Paragraph total = new Paragraph("Tổng tiền: " + order.getTotalAmount() + "VNĐ", fontTitle);
            total.setSpacingBefore(15f);
            document.add(total);
        } catch (DocumentException | IOException ex) {
            throw new RuntimeException("Lỗi khi tạo PDF: " + ex.getMessage(), ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public Map<String, Long> countOrdersByStatus() {
        List<Order> orders = orderRepository.findAll();
        Map<String, Long> map = orders.stream().collect(Collectors.groupingBy(o -> o.getOrderStatus().name()
                , Collectors.counting()));
        return map;
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(null);
        orderRepository.delete(order);
    }

    @Override
    public void changeStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Not found orderid in database"));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }
}
