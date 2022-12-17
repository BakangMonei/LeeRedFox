package controller.command.impl;

import controller.attribute.RequestMethod;
import controller.attribute.RequestParameterName;
import controller.attribute.SessionAttributeName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import by.belotskiy.movie_star.controller.command.ActionCommand;
import by.belotskiy.movie_star.controller.command.CommandResult;
import by.belotskiy.movie_star.controller.path.UrlPath;
import by.belotskiy.movie_star.exception.CommandException;
import by.belotskiy.movie_star.exception.ServiceException;
import by.belotskiy.movie_star.model.entity.Like;
import by.belotskiy.movie_star.model.entity.User;
import by.belotskiy.movie_star.model.service.LikeService;
import by.belotskiy.movie_star.model.service.factory.ServiceFactory;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

/**
 * Action command sends like
 *
 * @deprecated (“replaced with ajaxServlet”)
 */
public class LikeCommand implements ActionCommand {
    LikeService likeService = ServiceFactory.getInstance().getLikeService();
    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        if(request.getMethod().equals(RequestMethod.POST)){
            int reviewId = Integer.parseInt(request.getParameter(RequestParameterName.REVIEW_ID));
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(SessionAttributeName.USER);
            int userId = user.getId();
            try {
                Optional<Like> optionalLike = likeService.findLike(userId, reviewId);
                Like like;
                if(optionalLike.isPresent()){
                    like = optionalLike.get();
                    if(like.isLike()){
                        likeService.deleteLike(userId,reviewId);
                    }else{
                        like.setLike(true);
                        likeService.update(like);
                    }
                }else{
                    like = new Like(userId, reviewId, true);
                    likeService.update(like);
                }
            } catch (ServiceException e) {
                throw new CommandException(e);
            }
        }
        return new CommandResult(UrlPath.HOME_DO, CommandResult.Type.RETURN_URL);
    }
}
