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
    <v-row justify="center" align="center">
        <v-col cols="12" sm="8" md="6">
            <v-card class="logo py-4 d-flex justify-center">
                <vrd-springboot-logo/>
                <vrd-nuxt-logo/>
                <vrd-vuetify-logo/>
            </v-card>
            <v-card>
                <v-card-title class="headline">{{$t('index.welcome')}}</v-card-title>
                <v-card-text>{{$t('index.sommaire')}}</v-card-text>
                <v-divider/>
                <v-card-text>
                    <p>{{$t('index.techno')}}</p>
                    <v-list lines="two">
                        <v-list-item v-for="item in items"
                            :key="item.techno"
                            :title="$t(item.description)"
                            :href="item.url">
                        </v-list-item>
                    </v-list>
                </v-card-text>
                <v-divider/>
                <v-card-text>
                    <a href="https://github.com/rdemers/org-rd-fullstack-springboot-nuxt" 
                       target="_blank" rel="noopener noreferrer">{{$t('index.scm')}}</a>
                </v-card-text>
            </v-card>
        </v-col>
    </v-row>
</template>

<script setup lang="ts">
    const items = [ 
        { techno: "SpringBoot", 
            description: "index.techno1",
            url: "https://spring.io/projects/spring-boot" },
        { techno: "Nuxt",
            description: "index.techno2",
            url: "https://nuxtjs.org/" },
        { techno: "Vue",
            description: "index.techno3",
            url: "https://vuejs.org/"},
        { techno: "Vuetify",
            description: "index.techno4",
            url: "https://next.vuetifyjs.com/en/"}
    ];
</script>

<style scoped>
</style>
