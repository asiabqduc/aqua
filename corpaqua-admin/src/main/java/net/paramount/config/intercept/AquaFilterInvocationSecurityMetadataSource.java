/**
 * 
 */
package net.paramount.config.intercept;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

/**
 * @author ducbq
 *
 */
public class AquaFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
	private AntPathMatcher urlMatcher = new AntPathMatcher();
	private static Map <String,Collection <ConfigAttribute>> resourceMap = null;
	
	@Inject
  private JdbcTemplate commonDao;

	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		if(resourceMap==null) {
			loadResourceDefine();
		}
			// TODO Auto-generated method stub
		String url =((FilterInvocation)object).getRequestUrl();
		if(resourceMap != null){
			Set<String> urlPatternSet = resourceMap.keySet();
			for(String urlPattern:urlPatternSet){
				if(urlMatcher.match(urlPattern, url)){
					return resourceMap.get(urlPattern);
				}
			}
		}
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	public boolean isResourceMapEmpty(){
		if(resourceMap==null){
			return true;
		}else{
			return false;
		}
	}


	public String resourceSelfMatcher(String resURL){
		return null;
	}

	/**
	 * Refresh resource allocation
	 */
	public void refreshResource(){
		loadResourceDefine();
	}

	/**
	 * Load URL permission configuration
	 */
	private void loadResourceDefine()  {
		long starttime = new Date().getTime();
		String sql = "SELECT * FROM auth_resource";
		resourceMap = new HashMap<String,Collection <ConfigAttribute >> ();
		/*
		List<Properties> resourceList = commonDao.queryForList(sql);
		List<Map> roleList = commonDao.queryForList("SELECT * FROM auth_role");

		Map<String,String> roleIdMap = new HashMap();
		for(Map roleMap:roleList){
			String roleId = roleMap.get("id").toString();
			String rolename = roleMap.get("rolename").toString();
			roleIdMap.put(roleId, rolename);
		}

		long endtime = new Date().getTime();
		System.out.println("Complete query, time-consuming"+(endtime-starttime)+"ms");
		for(Map dataMap:resourceList){
			String urlPattern ="";
			if(dataMap.get("url_pattern")!=null){
				urlPattern = dataMap.get("url_pattern").toString();
			}
			String[] roleIds = new String[0];
			if(dataMap.get("access_role")!=null&&!dataMap.get("access_role").equals("")){
				String acce***ole = dataMap.get("access_role").toString();
				roleIds = acce***ole.split(",");
			}

			Collection <ConfigAttribute> atts  = new ArrayList < ConfigAttribute >();
			for(String roleId:roleIds){
				ConfigAttribute ca = new SecurityConfig(roleIdMap.get(roleId));
				atts.add(ca);
			}
			resourceMap.put(urlPattern,atts);
		}
		endtime = new Date().getTime();
		System.out.println("Loading system permission configuration is complete, time-consuming"+(endtime-starttime)+"ms");
		*/
	}
}
