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

<#-- Template for displaying list of user accounts -->

<#import "userAccounts-accountsNav.ftl" as p>

<form method="POST" action="${formUrls.add}" id="add-account" class="customForm" role="Add account">
    <h3><span>User accounts | </span><input type="submit" class="submit add-account" value="Add new account" /></h3>
</form>

<#if newUserAccount?? >
    <section class="account-feedback">
        <p>
            A new account for 
            <a href="${newUserAccount.editUrl}" title="new account">${newUserAccount.firstName} ${newUserAccount.lastName}</a>
            was successfully created. 
            <#if emailWasSent?? > 
                A notification email has been sent to ${newUserAccount.emailAddress}
                with instructions for activating the account and creating a password.
            </#if>
        </p>
    </section>
</#if>

<#if updatedUserAccount?? >
    <section class="account-feedback">
        <p>
            The account for 
            <a href="${updatedUserAccount.editUrl}" title="updated account">${updatedUserAccount.firstName} ${updatedUserAccount.lastName}</a>
            has been updated.
            <#if emailWasSent?? > 
                A confirmation email has been sent to ${updatedUserAccount.emailAddress}
                with instructions for resetting a password. 
                The password will not be reset until the user follows the link provided in this email.
            </#if>
        </p>
    </section>
</#if>

<#if deletedAccountCount?? >
    <section class="account-feedback">
        <p>
            Deleted ${deletedAccountCount} accounts.
        </p>
    </section>
</#if>

<section id="filter-roles">
    <form method="POST" action="${formUrls.list}" class="customForm" role="filter by roles">
        <select name="roleFilterUri" id="roleFilterUri">
            <option value="" <#if roleFilterUri = "">selected</#if> >Filter by roles</option>
            <#list roles as role>
            <option value="${formUrls.list}?roleFilterUri=${role.uri?url}" <#if roleFilterUri = role.uri>selected</#if> >${role.label}</option>
            </#list>
            <!--
            When roleFilterUri or searchTerm changes,
            pageIndex should be set to 1. When any of these changes (including pageIndex), the form 
            should be submitted.
            -->
        </select>
        
        <#if roleFilterUri?has_content>
             <span><a href="${formUrls.list}" title="view all acounts"> View all accounts</a></span>
        </#if>
    </form>
</section>

<section id="search-accounts">
    <form method="POST" action="${formUrls.list}" class="customForm" role="search accounts">
        <input type="text" name="searchTerm" />
        <input class="submit" type="submit" value="Search accounts"/>
        <!--
            When searchTerm changes, 
            set pageIndex to 1
            set orderDirection to "ASC"
            set orderField to "email" 
            submit the form (submit action is "list") 
        -->
    </form>
</section>
<#if searchTerm?has_content>
<section id="search-feedback">
    <p>Search results for "<strong>${searchTerm}</strong>" | <span><a href="${formUrls.list}" title="view all accounts"> View all accounts</a></span></p>
</section>
</#if>
<form method="POST" action="${formUrls.list}" id="account-display" class="customForm" role="accounts display">
    <@p.accountsNav />
    
    <table id="account">
        <caption>Account Management</caption>

        <thead>
            <tr>
               <th scope="col"> <input  class="delete-all hidden" type="checkbox" name="delete-all">
                   Email Address
                   <nav class="account-alpha-browse">
                       <a class="sort-asc" href="?accountsPerPage=${accountsPerPage}&orderField=email&orderDirection=ASC" title="ascending order"></a> 
                       <a class="sort-desc" href="?accountsPerPage=${accountsPerPage}&orderField=email&orderDirection=DESC" title="descending order"></a>
                   </nav>
                </th>
                
                <th scope="col">
                    First name
                    <nav class="account-alpha-browse">
                        <a class="sort-asc" href="?accountsPerPage=${accountsPerPage}&orderField=firstName&orderDirection=ASC" title="ascending order"></a> 
                        <a class="sort-desc" href="?accountsPerPage=${accountsPerPage}&orderField=firstName&orderDirection=DESC" title="descending order"></a>
                    </nav>
                </th>
                
                <th scope="col">
                    Last name
                    <nav class="account-alpha-browse">
                        <a class="sort-asc" href="?accountsPerPage=${accountsPerPage}&orderField=lastName&orderDirection=ASC" title="ascending order"></a> 
                        <a class="sort-desc" href="?accountsPerPage=${accountsPerPage}&orderField=lastName&orderDirection=DESC" title="descending order"></a>
                    </nav>
                </th>
                
                <th scope="col">
                    Status
                    <nav class="account-alpha-browse">
                        <a class="sort-asc" href="?accountsPerPage=${accountsPerPage}&orderField=status&orderDirection=ASC" title="ascending order"></a> 
                        <a class="sort-desc" href="?accountsPerPage=${accountsPerPage}&orderField=status&orderDirection=DESC" title="descending order"></a>
                    </nav>
                </th>
                
                <th scope="col">Roles</th>
                
                <th scope="col">
                    Login&nbsp;count
                    <nav class="account-alpha-browse">
                        <a class="sort-asc" href="?accountsPerPage=${accountsPerPage}&orderField=count&orderDirection=ASC" title="ascending order"></a> 
                        <a class="sort-desc" href="?accountsPerPage=${accountsPerPage}&orderField=count&orderDirection=DESC" title="descending order"></a>
                    </nav>
                </th>

                <th scope="col">
                    Last&nbsp;Login
                    <nav class="account-alpha-browse">
                        <a class="sort-asc" href="?accountsPerPage=${accountsPerPage}&orderField=lastLogin&orderDirection=ASC" title="ascending order"></a> 
                        <a class="sort-desc" href="?accountsPerPage=${accountsPerPage}&orderField=lastLogin&orderDirection=DESC" title="descending order"></a>
                    </nav>
                </th>
            </tr>
        </thead>
    
        <tbody>
            <#list accounts as account>
                <tr>
                    <td>
                        <#if account.deletable>
                            <input type="checkbox" name="deleteAccount" value="${account.uri}" title="select this account to delete it"/>
                            <#assign disableDeleteAccount = '' />
                            <!-- ignored unless submit action is formUrls.delete -->
                        <#else>
                             <#assign disableDeleteAccount = 'class="disable-delete"' />
                        </#if>
    
                        <#if account.editUrl != "">
                            <a ${disableDeleteAccount} href="${account.editUrl}"  title="click to view account details">${account.emailAddress}</a>
                            <!-- when this link is clicked, editAccount is noticed and all other fields are ignored. -->
                        <#else>
                            ${account.emailAddress}
                        </#if>
                    </td>
                    <td>${account.firstName}</td>
                    <td>${account.lastName}</td>
                    <td>${account.status}</td>
                    <td>
                        <#list account.permissionSets as permissionSet>
                            <div>${permissionSet}</div>
                        </#list>
                    </td>
                    <td>${account.loginCount}</td>
                    <td>
                        <#if account.lastLoginTime??>
                            ${account.lastLoginTime?date?string.medium}
                            <br />${account.lastLoginTime?time?string.short}
                        </#if>
                    </td>
                </tr>
            </#list>
        </tbody>
    </table>
    
    <@p.accountsNav />
</form>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/account/account.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/account/accountUtils.js"></script>')}