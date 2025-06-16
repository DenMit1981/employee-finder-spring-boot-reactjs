package com.empire.employeefinder.service;

import com.empire.employeefinder.dto.response.SelectionResponseDto;
import com.empire.employeefinder.dto.response.UserRegisterResponseDto;

public interface EmailService {

    void sendRegistrationDetails(UserRegisterResponseDto userDto);

    void sendSelectionDetails(SelectionResponseDto selectionDto);
}
