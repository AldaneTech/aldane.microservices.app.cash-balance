package net.aldane.cash_balance;

import net.aldane.cash_balance.mapper.*;
import net.aldane.cash_balance.repository.db.*;
import net.aldane.cash_balance.repository.db.entity.*;
import net.aldane.cash_balance.service.*;
import net.aldane.cash_balance.utils.AuthUtils;
import net.aldane.cash_balance_api_server_java.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogDescriptionTest {
    private final AuthUtils auth = mock(AuthUtils.class);
    private final UserDb user = new UserDb();
    CatalogDescriptionTest() { user.setId(7L); when(auth.getUser()).thenReturn(user); }
    @Test void categoryDescriptionCanBeClearedAndOmittedDescriptionIsPreserved() {
        var repository = mock(CategoryDbRepository.class);
        var entity = new CategoryDb(); entity.setId(1L); entity.setUser(user); entity.setComment("Old");
        when(repository.findById(1L)).thenReturn(Optional.of(entity)); when(repository.save(entity)).thenReturn(entity);
        var service = new CategoryServiceImpl(repository, Mappers.getMapper(CategoryMapper.class));
        ReflectionTestUtils.setField(service, "authUtils", auth);
        assertEquals("Old", service.updateCategory(new Category().id(1L).name("Renamed")).getComment());
        assertEquals("", service.updateCategory(new Category().id(1L).comment("")).getComment());
    }
    @Test void brandDescriptionCanBeCleared() {
        var repository = mock(BrandDbRepository.class);
        var entity = new BrandDb(); entity.setId(1L); entity.setUser(user); entity.setComment("Old");
        when(repository.findById(1L)).thenReturn(Optional.of(entity)); when(repository.save(entity)).thenReturn(entity);
        var service = new BrandServiceImpl(repository, Mappers.getMapper(BrandMapper.class));
        ReflectionTestUtils.setField(service, "authUtils", auth);
        assertEquals("", service.updateBrand(new Brand().id(1L).comment("")).getComment());
    }
    @Test void storeDescriptionCanBeCleared() {
        var repository = mock(StoreDbRepository.class);
        var entity = new StoreDb(); entity.setId(1L); entity.setUser(user); entity.setComment("Old");
        when(repository.findById(1L)).thenReturn(Optional.of(entity)); when(repository.save(entity)).thenReturn(entity);
        var service = new StoreServiceImpl(repository, Mappers.getMapper(StoreMapper.class));
        ReflectionTestUtils.setField(service, "authUtils", auth);
        assertEquals("", service.updateStore(new Store().id(1L).comment("")).getComment());
    }
}
