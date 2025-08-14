<!--
  * Copyright 2023; Réal Demers.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *    http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  -->
<template>
    <v-row align="center" justify="center">
        <v-card max-width="500">
            <v-alert v-if=isErrorLogin color="error" icon="$error" 
                     :title="$t('login.title')"  :text="$t('login.alert.text')"/>
            <v-card-title align="center" primary-title>{{$t('login.title')}}</v-card-title>
            <v-card-text align="center">{{$t('login.information')}}</v-card-text>
            <v-divider/>
            <v-card-text>
                <v-form>
                    <v-text-field prepend-icon="mdi-account" id="login" name="login"    
                                  :label="$t('login.login')" type="text" v-model="loginRequest.username"></v-text-field>
                    <v-text-field prepend-icon="mdi-lock" id="password" name="password" 
                                  :label="$t('login.password')" type="password" v-model="loginRequest.password"></v-text-field>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <v-progress-circular v-if=isActionDisabled indeterminate color="primary"/>
                <v-btn color="success" class="mx-2" :disabled=isActionDisabled flat @click="doLogin">{{$t('common.button.login')}}</v-btn>
            </v-card-actions>
        </v-card>
    </v-row>
</template>

<script setup lang="ts">
    import type LoginRequest       from "@/types/LoginRequest";
    import type ResponseData       from "@/types/ResponseData";
    import type Session            from "@/types/Session";

    import { SessionStore }        from "@/store/SessionStore";
    import authentificationService from "@/services/AuthentificationService";

    const isActionDisabled = ref<boolean>(false);
    const isErrorLogin     = ref<boolean>(false);
    const loginRequest     = ref<LoginRequest>({username: "", password: ""});

    function doLogin(): void {
        isActionDisabled.value = true;

        authentificationService.doLogin(loginRequest.value)
            .then((response: ResponseData) => {
                const session:Session = SessionStore().getSession;

                session.jwtToken = response.data.message;
                SessionStore().setSession(session);
                
                isErrorLogin.value = false;
                navigateToHome(); // Nuxt/NavigaTo() constraint - return or await.
            })
            .catch((error: any) => {
                isErrorLogin.value = true;
                if (error.response) {
                    console.log(error.response.data);
                    console.log(error.response.status);
                    console.log(error.response.headers);
                } else if (error.request) {
                    console.log(error.request);
                } else {
                    console.log(error);
                }
            })
            .finally(() => {
                isActionDisabled.value = false;
            });
    }    

    function navigateToHome(): any {
        return navigateTo("/");
    }
</script>

<style scoped>
</style>
