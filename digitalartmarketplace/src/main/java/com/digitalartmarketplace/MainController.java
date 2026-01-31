package com.digitalartmarketplace;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

    private static final String UPLOAD_DIR =
            "src/main/resources/static/uploads/";

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private UserService userService;

    // ---------------- HOME ----------------
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // ---------------- LOGIN ----------------
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ---------------- REGISTER ----------------
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {
        userService.register(user);
        return "redirect:/login";
    }

    // ---------------- DASHBOARD ----------------
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // ---------------- UPLOAD FORM ----------------
    @GetMapping("/upload")
    public String uploadForm(Model model) {
        model.addAttribute("artwork", new Artwork());
        return "upload";
    }

    // ---------------- UPLOAD ARTWORK ----------------
    @PostMapping("/upload")
    public String uploadArtwork(
            Artwork artwork,
            @RequestParam("image") MultipartFile imageFile,
            Principal principal) {

        try {
            if (imageFile.isEmpty()) {
                return "redirect:/upload";
            }

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName =
                    System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, imageFile.getBytes());

            User user = userService.findByEmail(principal.getName());

            artwork.setImageName(fileName);
            artwork.setArtist(user);
            artworkService.save(artwork);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/gallery";
    }

    // ---------------- GALLERY ----------------
    @GetMapping("/gallery")
    public String gallery(Model model, Principal principal) {

        List<Artwork> artworks = artworkService.findAll();
        User currentUser = userService.findByEmail(principal.getName());

        model.addAttribute("artworks", artworks);
        model.addAttribute("currentUser", currentUser);

        return "gallery";
    }

    // ---------------- DELETE ARTWORK ----------------
    @PostMapping("/artwork/delete/{id}")
    public String deleteArtwork(
            @PathVariable int id,
            Principal principal) {

        User loggedInUser = userService.findByEmail(principal.getName());
        Artwork artwork = artworkService.findById(id);

        if (loggedInUser != null &&
            artwork != null &&
            "ARTIST".equals(loggedInUser.getRole()) &&
            artwork.getArtist() != null &&
            artwork.getArtist().getId() == loggedInUser.getId()) {

            artworkService.deleteById(id);
        }

        return "redirect:/gallery";
    }

    // ---------------- BUY ----------------
    @GetMapping("/buy/{id}")
    public String buyArtwork(@PathVariable int id, Model model) {

        Artwork artwork = artworkService.findById(id);
        model.addAttribute("artwork", artwork);

        return "buy";
    }

    // ---------------- UPI ----------------
    @GetMapping("/upi")
    public String upiPage() {
        return "upi";
    }

    // ---------------- PAYMENT SUCCESS ----------------
    @GetMapping("/payment-success")
    public String paymentSuccess() {
        return "redirect:/dashboard";
    }

    // ---------------- PROFILE ----------------
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        if ("ARTIST".equals(user.getRole())) {
            List<Artwork> myArtworks = artworkService.findByArtist(user);
            model.addAttribute("myArtworks", myArtworks);
        }

        return "profile";
    }

    // ---------------- EDIT PROFILE (GET) ----------------
    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        return "profile-edit";
    }

    // ---------------- EDIT PROFILE (POST) ----------------
    @PostMapping("/profile/edit")
    public String updateProfile(
            @RequestParam(value = "image", required = false) MultipartFile image,
            User updatedUser,
            Principal principal) {

        try {
            User existingUser = userService.findByEmail(principal.getName());

            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());

            if (image != null && !image.isEmpty()) {

                String uploadDir = "src/main/resources/static/uploads/profile/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName =
                        System.currentTimeMillis() + "_" + image.getOriginalFilename();

                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, image.getBytes());

                existingUser.setProfileImage(fileName);
            }

            userService.save(existingUser);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/profile";
    }
}
