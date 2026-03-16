package com.project.edugov.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.edugov.model.Notification;
import com.project.edugov.repository.NotificationRepository;

@Service

public class NotificationService {

	@Autowired

	private NotificationRepository repository;

	public Notification send(Notification notification) {

		return repository.save(notification);

	}

	public void broadcast(List<Long> userIDs, Notification.Category category, String msg) {

		userIDs.forEach(id -> {

			Notification n = new Notification();

			n.setUserID(id);

			n.setCategory(category);

			n.setMessage(msg);()

			repository.save(n);

		});

	}

	public Notification markAsRead(Long id) {

		Notification n = repository.findById(id).orElseThrow();

		n.setStatus("READ");

		return repository.save(n);

	}

}