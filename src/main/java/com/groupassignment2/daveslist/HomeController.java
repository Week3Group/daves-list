package com.groupassignment2.daveslist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    RoomRepository roomRepository;

    @RequestMapping("/")
    public String showIndex(Model model){
        return "index";
    }

    @GetMapping("/add")
    public String showAddRoomForm(Model model){
        model.addAttribute("aRoom",new Room());
        return"/add";
    }

    @PostMapping("/process")
    public String processRoom(@Valid @ModelAttribute("aRoom") Room room, BindingResult result){
        if(result.hasErrors())
        {
            return "add";
        }

        else{
            roomRepository.save(room);
            return "redirect:/list";
        }

    }

    @RequestMapping("/list")
    public String listRooms(Model model){
        model.addAttribute("rooms",roomRepository.findAll());
        return"/list";
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
}
