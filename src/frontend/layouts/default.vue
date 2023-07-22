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
    <v-app>
        <v-navigation-drawer v-model="isDrawer" :mini-variant="miniVariant" :clipped="clipped" fixed app>
            <v-list>
                <v-list-item v-for="(menu, i) in menus" :key="i" :title="$t(menu.title)" :to="menu.to" router exact>
                    <v-list-item-action>
                        <v-icon>{{menu.icon}}</v-icon>
                    </v-list-item-action>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>
        <v-app-bar :clipped-left="clipped" fixed app>
            <v-app-bar-nav-icon :disabled=isActionDisabled @click="swapDrawer()"/>
            <v-toolbar-title v-text="title"/>
            <v-spacer/>
            <v-menu offset-y>
                <template v-slot:activator="{props}">
                    <v-btn flat v-bind="props">
                        <v-icon left>mdi-translate-variant</v-icon>
                        <span class="mx-4 text-capitalize">{{currentlocale}}</span>
                    </v-btn>
                </template>
                <v-list>
                    <v-list-item v-for="(locale, i) in locales" :key=i @click="changeLocale(locale as LocaleObject)">
                        <v-list-item-title v-text="(locale as LocaleObject).name"/>
                    </v-list-item>
                </v-list>
            </v-menu>
            <v-btn icon :disabled=isActionDisabled @click="doLogout()">
                <v-icon>mdi-logout</v-icon> 
            </v-btn>
        </v-app-bar>
        <v-main>
            <v-container>
                <slot/>
            </v-container>
        </v-main>
        <v-footer :absolute="!fixed" app>
            <span>Copyright &copy; {{new Date().getFullYear()}}</span>
        </v-footer>
    </v-app>
</template>

<script setup lang="ts">
    import { computed, ref, onMounted } from "vue";

    import Menu                    from "@/types/Menu";
    import { SessionStore }        from "@/store/SessionStore";
    import { LocaleObject }        from "@nuxtjs/i18n/dist/runtime/composables";
    import authentificationService from "@/services/AuthentificationService";

    const title:string = "org.rd.fullstack.springboot-nuxt";
    const menus:Menu[] = [ 
        { icon: "mdi-home",            title: "layout.home",       to: "/" },
        { icon: "mdi-book",            title: "layout.books",      to: "/books" },
        { icon: "mdi-developer-board", title: "layout.scratchpad", to: "/scratchpad/ui" },
        { icon: "mdi-copyright",       title: "layout.about",      to: "/about" }
    ];

    const clipped     = ref<boolean>(false);
    const drawer      = ref<boolean>(false);
    const fixed       = ref<boolean>(false);
    const miniVariant = ref<boolean>(false);

    const { locale, locales, setLocale } = useI18n();

    function changeLocale(locale:LocaleObject): void {
        setLocale(locale.code);            
    }

    function doLogout(): any {
        if (authentificationService.doLogout()) {
            drawer.value = false;
            return navigateTo("/login");
        }
    }

    function swapDrawer(): void {
        drawer.value = !drawer.value;
    }

    const isDrawer = computed<boolean>(() => {
        if (! SessionStore().isActiveSession()) {
            return false;
        }
        return drawer.value;
    })

    const isActionDisabled = computed<boolean>(() => {
        return (! SessionStore().isActiveSession());
    })

    const currentlocale = computed<string|undefined>(() => {
        const localeArray:LocaleObject[] = locales.value as LocaleObject[];
        const lo:LocaleObject|undefined = localeArray.find(i => i.code === locale.value);
        return lo?.name;
    })

    onMounted(() => {
        console.log("Example of a lifecycle Hooks.");
    })
</script>

<style scoped>
</style>