package com.hodos.hermes.service.impl;

import com.hodos.hermes.athena.BlogScrolls;
import com.hodos.hermes.athena.UserScrolls;
import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.blog.BlogDto;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.BlogService;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl implements BlogService {
    private final BlogScrolls blogScrolls;
    private final UserScrolls userScrolls;

    public BlogServiceImpl(BlogScrolls blogScrolls, UserScrolls userScrolls) {
        this.blogScrolls = blogScrolls;
        this.userScrolls = userScrolls;
    }

    public void addNewBlog(BlogDto blogDto){
        Long travellerId = blogDto.getTravellerId();

        if(travellerId == null )
            throw new CustomException(ErrorTypes.REQUIRED,"Traveller Id required");

        User user = userScrolls.findById(travellerId).orElseThrow(()->
                new CustomException(ErrorTypes.NOT_FOUND,"Traveller not found"));



    }

}

