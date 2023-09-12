package net.conczin.archlink.dto;

public record ErrorModel(String name, String message, String path, Long timestamp) {
}