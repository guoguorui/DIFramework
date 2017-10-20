package mapper;

import annotations.Select;

//需要通用接口
public interface UserService {
	
	@Select("select password from users where name='GGR'")
	public String findPasswordByName();
}
