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

// Update the import path to the correct relative location
import { SessionStore } from "@/store/SessionStore";

export default defineNuxtRouteMiddleware((to, from) => {

    const authPage:string = "/login";
    if ((to.fullPath !== authPage) && 
        (! SessionStore().isActiveSession())) {
        return navigateTo(authPage, {replace:true});
    }

});