### Config 

when ever you make a configuration to your app
you make a class


The configuration creates a Servlet Filter known as the springSecurityFilterChain

@EnableWebSecurity tells Spring:
"Enable Spring Security's web security support and use the security configuration I provide."

without it Spring Security won't use it to configure the web security filter chain.

