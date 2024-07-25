/*
 * Copyright 2023, 2024; Réal Demers.
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

import { defineStore } from "pinia";
import type Session    from "@/types/Session";

export const SessionStore = defineStore({
    id: "SessionStore",

    state: () => ({
        currentSession: {
            jwtToken: "",
            user: "",
            roles: [""]
        } as Session,
    }),

    getters: {
        getSession():Session {
            return this.currentSession;
        }
    },

    actions: {
        setSession(session: Session) {
            this.currentSession = session;
        },

        clearSession() {
            this.currentSession = {
                jwtToken: "",
                user: "",
                roles: [""]
            }
        },
 
        isActiveSession(): boolean {
            if (this.currentSession.jwtToken.trim().length !== 0) {
                return true;
            }
            return false;
        }
    }
})
