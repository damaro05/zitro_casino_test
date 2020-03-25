package org.zitroprueba.casino.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
public class OAuth2SecurityConfiguration {

	@Configuration
	@EnableWebSecurity
	protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
		
		@Autowired
		private UserDetailsService userDetailsService;
		
		@Autowired
		protected void registerAuthentication(
				final AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}
	}
	
	/**
	 *	This method is used to configure who is allowed to access which parts of our
	 *	resource server (i.e. the "/game" endpoint) 
	 */
	@Configuration
	@EnableResourceServer
	protected static class ResourceServer extends
			ResourceServerConfigurerAdapter {

		private static final String SLOT_ID = "slot";

		// This method configures the OAuth scopes required by clients to access
		@Override
		public void configure(HttpSecurity http) throws Exception {
			
			http.csrf().disable();
			
			http
			.authorizeRequests()
				.antMatchers("/oauth/token").anonymous();
			
			// Require all GET requests to have client "read" scope
			http
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/**")
				.access("#oauth2.hasScope('read')");
			
			// Require all other requests to have "write" scope
			http
			.authorizeRequests()
				.antMatchers("/**")
				.access("#oauth2.hasScope('write')");
		}
		
	    @Override
	    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
	         resources.resourceId(SLOT_ID);
	    }

	}

	/**
	 * This class is used to configure how our authorization server (the "/oauth/token" endpoint) 
	 * validates client credentials.
	 */
	@Configuration
	@EnableAuthorizationServer
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	protected static class OAuth2Config extends
			AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		// A data structure used to store both a ClientDetailsService and a UserDetailsService
		private ClientAndUserDetailsService combinedService_;

		/**
		 * 
		 * This constructor is used to setup the clients and users that will be able to login to the
		 * system. This is a VERY insecure setup that is using hard-coded lists of clients / users /
		 * passwords and should never be used for anything other than local testing
		 * on a machine that is not accessible via the Internet.
		 */
		public OAuth2Config() throws Exception {	
			
			// Create a service that has the credentials for all our clients
			ClientDetailsService csvc = new InMemoryClientDetailsServiceBuilder()
					// Create a client that has "read" and "write" access to the
					
					.withClient("mobile").authorizedGrantTypes("password")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read","write").resourceIds("slot")
					.and()
					// Create a second client that only has "read" access to the service
					.withClient("mobileReader").authorizedGrantTypes("password")
					.authorities("ROLE_CLIENT")
					.scopes("read").resourceIds("slot")
					.accessTokenValiditySeconds(3600).and().build();

			// Create a series of hard-coded users. 
			UserDetailsService svc = new InMemoryUserDetailsManager(
					Arrays.asList(
							User.create("admin", "pass", "ADMIN", "USER"),
							User.create("user0", "pass", "USER"),
							User.create("user1", "pass", "USER"),
							User.create("user2", "pass", "USER"),
							User.create("user3", "pass", "USER"),
							User.create("user4", "pass", "USER"),
							User.create("user5", "pass", "USER")));

			combinedService_ = new ClientAndUserDetailsService(csvc, svc);
		}

		/**
		 * Return the list of trusted client information to anyone who asks for it.
		 */
		@Bean
		public ClientDetailsService clientDetailsService() throws Exception {
			return combinedService_;
		}

		/**
		 * Return all of our user information to anyone in the framework who requests it.
		 */
		@Bean
		public UserDetailsService userDetailsService() {
			return combinedService_;
		}

		/**
		 * This method tells our AuthorizationServerConfigurerAdapter to use the delegated AuthenticationManager
		 * to process authentication requests.
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		/**
		 * This method tells the AuthorizationServerConfigurerAdapter to use our self-defined client details service to
		 * authenticate clients with.
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.withClientDetails(clientDetailsService());
		}

	}
	
	

}
