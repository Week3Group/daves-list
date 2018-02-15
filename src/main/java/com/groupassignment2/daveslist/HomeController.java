package com.groupassignment2.daveslist;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @Autowired
    private UserService userService;


    @RequestMapping("/")
    public String showIndex(Model model){
        return "index";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/add")
    public String showAddRoomForm(Model model){
        model.addAttribute("room",new Room());
        return"/add";
    }

    @PostMapping("/process")
    public String processRoom(@Valid @ModelAttribute("room") Room room, @RequestParam("file")MultipartFile file, BindingResult result){

        if(file.isEmpty()){
            return "add";
        }
        try{
            Map uploadResult=cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype","auto"));
            room.setImageUrl(uploadResult.get("url").toString());
        }catch (IOException e){
            e.printStackTrace();
            return "add";
        }
        if(result.hasErrors())        {

            return "add";

        }
        else{
            room.setRented("No");
            roomRepository.save(room);
            return "redirect:/list";
        }

    }

    @RequestMapping("/list")
    public String listRooms(Model model){
        model.addAttribute("rooms",roomRepository.findAll());
        return"list";
    }

    @RequestMapping("/detail/{id}")
    public String showDetail(@PathVariable("id")Long id,Model model){
        model.addAttribute("room",roomRepository.findOne(id));
        return "showroomdetails";
    }

    @RequestMapping("/update/{id}")
    public String updateDetail(@PathVariable("id")Long id,Model model){
        model.addAttribute("room",roomRepository.findOne(id));
        return "add";
    }

    @RequestMapping("/rent/{id}")
    public String rentRoom(@PathVariable("id") long id,Model model,RedirectAttributes redirectAttributes){
        model.addAttribute("room",roomRepository.findOne(id));

        Room room=roomRepository.findOne(id);

        room.setRented("Yes");

        String roomRentMessage="\""+room.getAddress()+"\""+" is rented";

        redirectAttributes.addFlashAttribute("message1", roomRentMessage);

        model.addAttribute("aRoom", roomRepository.findOne(id));
        roomRepository.save(room);
        return "redirect:/list";
    }
    @RequestMapping("/available/{id}")
    public String availableRoom(@PathVariable("id") long id,Model model,RedirectAttributes redirectAttributes){
        model.addAttribute("room",roomRepository.findOne(id));

        Room room=roomRepository.findOne(id);

        room.setRented("No");

        String roomRentMessage="\""+room.getAddress()+"\""+" is available";

        redirectAttributes.addFlashAttribute("message1", roomRentMessage);

        model.addAttribute("aRoom", roomRepository.findOne(id));
        roomRepository.save(room);
        return "redirect:/list";
    }

    //For user registration
    @RequestMapping(value="/register",method=RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user",new User());
        return "registration";
    }


    @RequestMapping(value="/register",method= RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("User") User user, BindingResult result, Model model){
        model.addAttribute("user",user);
        if(result.hasErrors()){
            return "registration";
        }else{
            userService.saveUser(user);
            model.addAttribute("message","User Account Successfully Created");
        }
        return "index";
    }
}
