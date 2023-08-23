package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        List<CommentDto> commentDtos = comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());
        Long requestId;

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        } else {
            requestId = null;
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                commentDtos,
                requestId
        );
    }

    public static ItemForBookingDto toItemForBookingDto(Item item) {
        return new ItemForBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null
        );
    }

    public static ItemForItemRequestDto toItemForItemRequestDto(Item item) {
        Long requestId;

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        } else {
            requestId = null;
        }

        return new ItemForItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                requestId,
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                request
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getCreated(),
                comment.getAuthor().getName()
        );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }
}
