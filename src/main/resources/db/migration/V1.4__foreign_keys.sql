--
-- Name: inventory inventory_operating_system_id_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_operating_system_id_foreign FOREIGN KEY (operating_system_id) REFERENCES operating_systems(id);


--
-- Name: inventory inventory_service_type_id_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_service_type_id_foreign FOREIGN KEY (service_type_id) REFERENCES service_types(id);


--
-- Name: inventory inventory_who_create_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_who_create_foreign FOREIGN KEY (who_create) REFERENCES users(user_id);


--
-- Name: inventory inventory_who_update_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_who_update_foreign FOREIGN KEY (who_update) REFERENCES users(user_id);


--
-- Name: plogs plogs_session_id_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY plogs
    ADD CONSTRAINT plogs_session_id_foreign FOREIGN KEY (session_id) REFERENCES psessions(session_id);


--
-- Name: policy policy_who_create_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY policy
    ADD CONSTRAINT policy_who_create_foreign FOREIGN KEY (who_create) REFERENCES users(user_id);


--
-- Name: psessions psessions_inventory_id_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY psessions
    ADD CONSTRAINT psessions_inventory_id_foreign FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id);


--
-- Name: users users_tenant_id_foreign; Type: FK CONSTRAINT; Schema: public; Owner: pda
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_tenant_id_foreign FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id);



-- PostgreSQL database dump complete
