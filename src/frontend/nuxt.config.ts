/*
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
 */
export default defineNuxtConfig({
 
    ssr: false,
    //ssr: true,
    
    typescript: {
        strict: true
    },

    runtimeConfig: {
        "public": {
            "jwtURL": "http://localhost:8080/jwt",
            "apiURL": "http://localhost:8080/api"
        }
    },

    css: [  
        "vuetify/lib/styles/main.sass", 
        "@mdi/font/css/materialdesignicons.min.css",
        "@fortawesome/fontawesome-svg-core/styles.css"
    ],

    experimental: {
        payloadExtraction: false,
    },

    build: {
        transpile: ["vuetify"],
    },

    sourcemap: {
        "server": true,
        "client": true
    },
 
    modules: [
        [
            "@pinia/nuxt", {
                autoImports: [
                    "defineStore",
                    [ "defineStore", "definePiniaStore", "acceptHMRUpdate" ],
                ],
            },
        ],
        [
            '@nuxtjs/i18n', {
                lazy: true,
                strategy: "no_prefix",
                defaultLocale: "en",
                langDir: "lang",
                detectBrowserLanguage: {
                    useCookie: true,
                    cookieKey: "app_lang"
                },
                locales:[
                    {
                        code: "fr",
                        name: "Français",
                        file: "fr_CA.json",
                    },
                    {
                        code: "en",
                        name: "English",
                        file: "en_CA.json",
                    },
                ],
            },
        ],
    ],

    app: {
        baseURL: "/app",
        head: {
            title: "App",
            link: [{ 
                   rel: "icon", type: "image/x-icon", href: "/app/favicon.ico" 
               }
            ],
        },
    },

    vite: {
        define: {
            "process.env.DEBUG": false,
        },
    },
});
