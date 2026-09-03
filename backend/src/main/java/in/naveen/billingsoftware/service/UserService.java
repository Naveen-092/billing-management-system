package in.naveen.billingsoftware.service;

import java.util.List;

import in.naveen.billingsoftware.io.UserRequest;
import in.naveen.billingsoftware.io.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}