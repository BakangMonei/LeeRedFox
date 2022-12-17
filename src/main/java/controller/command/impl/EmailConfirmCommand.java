package controller.command.impl;

import controller.attribute.RequestParameterName;
import controller.attribute.SessionAttributeName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import by.belotskiy.movie_star.controller.command.ActionCommand;
import by.belotskiy.movie_star.controller.command.CommandResult;
import by.belotskiy.movie_star.controller.path.UrlPath;
import by.belotskiy.movie_star.exception.CommandException;
import by.belotskiy.movie_star.exception.ServiceException;
import by.belotskiy.movie_star.model.entity.User;
import by.belotskiy.movie_star.model.service.UserService;
import by.belotskiy.movie_star.model.service.factory.ServiceFactory;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

/**
 * Action command confirms email
 *
 */
public class EmailConfirmCommand implements ActionCommand {

    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        int userId = Integer.parseInt(request.getParameter(RequestParameterName.USER_ID));
        String token = (String)request.getAttribute(RequestParameterName.TOKEN);
        Optional<User> optionalUser;
        try{
            optionalUser = userService.confirmEmail(userId, token);
        }catch (ServiceException e){
            throw new CommandException(e);
        }
        if(optionalUser.isPresent()){
            HttpSession session = request.getSession();
            session.setAttribute(SessionAttributeName.USER, optionalUser.get());
        }
        return new CommandResult(UrlPath.PROFILE_DO, CommandResult.Type.REDIRECT);
    }
}
