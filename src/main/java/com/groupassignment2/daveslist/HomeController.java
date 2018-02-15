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

    @RequestMapping("/")
    public String showIndex(Model model){
        return "index";
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
        if(result.hasErrors())
        {
            System.out.println("fail");
            return "add";

        }
        else{
            room.setRented("No");
            roomRepository.save(room);
            System.out.println("success");
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

System.out.println(roomRentMessage);

        model.addAttribute("aRoom", roomRepository.findOne(id));
        roomRepository.save(room);
        return "redirect:/list";
    }
}
