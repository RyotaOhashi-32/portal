package com.portal.z.common.domain.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.portal.z.common.domain.model.User;
import com.portal.z.common.domain.model.Role;
import com.portal.z.common.domain.service.UserService;
import com.portal.z.common.domain.model.AppUserDetails;
import com.portal.z.common.domain.service.RoleService;

@Repository("LoginDaoImpl")
public class UserDetailsDaoImpl implements UserDetailsDao {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    /**
     * ユーザー情報を取得して、UserDetailsを生成するメソッド.
     */
    public UserDetails selectUserDetails(String user_id) {
    	
        //権限リストの取得（メソッド）
        List<GrantedAuthority> grantedAuthorityList = getRoleList(user_id);
        
        // 結果返却用のUserDetailsを生成
        AppUserDetails user = buildUserDetails(user_id, grantedAuthorityList);
        
        return user;
    }

    /**
     * 権限リストを取得するメソッド.
     */
    private List<GrantedAuthority> getRoleList(String user_id) {
    	
        //select実行(ユーザー権限の取得)
    	List<Role> authorityList = roleService.selectManyRole(user_id);
    	
		if (authorityList.isEmpty() == true ) {
	    	//ToDoここでuser_idが拾えなかったときの処理
			//
			//
			System.out.println("user_idが拾えなかった ：" + authorityList);
			throw new UsernameNotFoundException("user_idが拾えなかった ：");
			//return null;
		}

		System.out.println("user_id:" + authorityList);
		
        //結果返却用のList生成
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        
        //for(Map<String, Object> map: authorityList) {
        for(Role role: authorityList) {
        	
            //ロール名を取得
            String roleName = (String)role.getRole_name();

            //SimpleGrantedAuthorityインスタンスの生成
            GrantedAuthority authority =
                    new SimpleGrantedAuthority(roleName);

            //結果返却用のListにインスタンスを追加
            grantedAuthorityList.add(authority);
        }
        
        return grantedAuthorityList;
    }

    /**
     * ユーザークラスの作成.
     */
  private AppUserDetails buildUserDetails(String user_id_i,
                   List<GrantedAuthority> grantedAuthorityList) {
	  
	    User user_i = userService.selectOne(user_id_i);
	    
		if (user_i == null ) {
	    	//ToDoここでuser_idが拾えなかったときの処理
			//
			//
			
			System.out.println("user_id_idが拾えなかった ：" + user_i);
			return null;
		}
		
		System.out.println("user_id_id：" + user_i);

        // Mapから値を取得
        String user_id       = (String) user_i.getUser_id();
        Date user_due_date   = (Date) user_i.getUser_due_date();
        String password      = (String) user_i.getPassword();
        Date pass_update     = (Date) user_i.getPass_update();
        int login_miss_times = (Integer) user_i.getLogin_miss_times();
        boolean lock_flg     = (Boolean) user_i.isLock_flg();
        boolean enabled_flg  = (Boolean) user_i.isEnabled_flg();
        
        // 結果返却用のUserDetailsを生成
        //AppUserDetails user = new AppUserDetails().builder()
        new AppUserDetails();
		AppUserDetails user = AppUserDetails.builder()
                .user_id(user_id)
                .user_due_date(user_due_date)
                .password(password)
                .pass_update(pass_update)
                .login_miss_times(login_miss_times)
                .lock_flg(lock_flg)
                .enabled_flg(enabled_flg)
                .authority(grantedAuthorityList)
                .build();
		
        return user;
    }
}
