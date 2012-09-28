<#--
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<header id="branding" role="banner">
    <h1 class="vivo-logo"><a title="VIVO | enabling national networking of scientists" href="${urls.home}"><span class="displace">${siteName}</span></a></h1>
    <#-- Since we are using a graphic text for the tagline, we won't render ${siteTagline}
    <#if siteTagline?has_content>
        <em>${siteTagline}</em>
    </#if>-->

    <nav role="navigation">
        <ul id="header-nav" role="list">
            <li role="listitem"><a href="${urls.index}" title="index">Index</a></li>
            <#if user.loggedIn>
                <#-- COMMENTING OUT THE EDIT PAGE LINK FOR RELEASE 1.5. WE NEED TO IMPLEMENT THIS IN A MORE 
                     USER FRIENDLY WAY. PERHAPS INCLUDE A LINK ON THE PAGES THEMSELVES AND DISPLAY IF THE
                     USER IS A SITE ADMIN. tlw72
                     
                     <#if (page??) && (page?is_hash || page?is_hash_ex) && (page.URLToEditPage??) >
                        <li role="listitem"><a href="${page.URLToEditPage}" title="edit page">Edit Page</a></li>
                     </#if>
                -->
                <#if user.hasSiteAdminAccess>
                    <li role="listitem"><a href="${urls.siteAdmin}" title="site admin">Site Admin</a></li>
                </#if>
                    <li>
                        <ul class="dropdown">
                            <li id="user-menu"><a href="#" title="user">${user.loginName}</a>
                                <ul class="sub_menu">
                                     <#if user.hasProfile>
                                         <li role="listitem"><a href="${user.profileUrl}" title="my profile">My profile</a></li>
                                     </#if>
                                     <#if urls.myAccount??>
                                         <li role="listitem"><a href="${urls.myAccount}" title="my account">My account</a></li>
                                     </#if>
                                     <li role="listitem"><a href="${urls.logout}" title="log out">Log out</a></li>
                                </ul>
                            </li>
                         </ul>
                     </li>
                     

                
                ${scripts.add('<script type="text/javascript" src="${urls.base}/js/userMenu/userMenuUtils.js"></script>')}
                
            <#else>
                <li role="listitem"><a class="log-out" title="log in to manage this site" href="${urls.login}">Log in</a></li>
            </#if>
        </ul>
        
    </nav>
    
    <section id="search" role="region">
        <fieldset>
            <legend>Search form</legend>
            
            <form id="search-form" action="${urls.search}" name="search" role="search" accept-charset="UTF-8" method="POST"> 
                <div id="search-field">
                    <input type="text" name="querytext" class="search-vivo" value="${querytext!}" autocapitalize="off" />
                    <input type="submit" value="Search" class="search">
                </div>
            </form>
        </fieldset>
    </section>
</header>