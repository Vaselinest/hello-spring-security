package kr.ac.hansung.controller;

import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import kr.ac.hansung.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import kr.ac.hansung.entity.Product;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));

        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        Page<Product> productPage;
        if (normalizedKeyword != null) {
            productPage = productService.searchProducts(normalizedKeyword, pageRequest);
        } else {
            productPage = productService.getProducts(pageRequest);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());

        model.addAttribute("product", dto);
        model.addAttribute("productId", id); // 수정 폼 action URL을 위해 id 전달
        return "products/edit";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("product") ProductDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "products/edit";
        }
        productService.updateProduct(id, dto);
        return "redirect:/products";
    }
}
