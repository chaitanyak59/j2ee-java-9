package users.service;

import helpers.app.AppHelpers;
import helpers.app.AppResponse;
import users.dao.UsersDao;
import users.model.Users;

public class UsersService {
	private UsersDao usersDao;

	public UsersService() {
		usersDao = UsersDao.getUsersDaoInstance();
	}

	public AppResponse<Integer> createUserAccount(String name, String email, String password, String mobileNo,
			String address) {
		// Generate Salt,Hash
		Boolean isUserRegistered = usersDao.isUserExists(email);
		if (isUserRegistered) {
			return new AppResponse<Integer>(0, false);
		}
		byte[] salt = AppHelpers.getSalt();
		String hash = AppHelpers.getSecurePassword(password, salt);
		Users user = new Users(0, name, email, hash, AppHelpers.convertBtoString(salt), address, mobileNo, 0, null);
		// Store
		int status = usersDao.createUserAccount(user);
		return new AppResponse<Integer>(status, false);
	}

	public AppResponse<Boolean> isEmailRegistered(String email) {
		boolean status = usersDao.isUserExists(email);
		return new AppResponse<Boolean>(status, false);
	}

	public AppResponse<Users> authenticateUser(String email, String password) {
		Boolean isUserValid = usersDao.isUserExists(email);
		if (!isUserValid) {
			return new AppResponse<Users>(null, false);
		}
		Users user = usersDao.getUserLoginDetails(email);
		String computedHash = AppHelpers.getSecurePassword(password, user.getSalt().getBytes());
		if (computedHash.equals(user.getHash())) {
			return new AppResponse<Users>(user, false); // First Pay-load, Second Error if any
		} else {
			return new AppResponse<Users>(null, false);
		}
	}
}
