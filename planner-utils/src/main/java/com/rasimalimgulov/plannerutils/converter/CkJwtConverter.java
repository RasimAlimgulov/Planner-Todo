package com.rasimalimgulov.plannerutils.converter;




import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CkJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String,Object> claims=jwt.getClaims();


        Map<String,Object> realmAccess= (Map<String, Object>) claims.get("realm_access");
        if (realmAccess==null){
            return Collections.emptyList();
        }

        List<String> roles= (List<String>) realmAccess.get("roles");
        if (roles==null){
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> returnValues=roles.stream()
                .map(rolename->"ROLE_"+rolename)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return returnValues;
    }
}
