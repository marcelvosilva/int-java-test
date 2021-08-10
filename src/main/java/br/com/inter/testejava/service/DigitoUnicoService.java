package br.com.inter.testejava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.inter.testejava.cache.Cache;

@Service
public class DigitoUnicoService {

    @Autowired
    private Cache cache;

    public Integer uniqueDigit(String n, Integer k) throws Exception {

        Long soma = 0l;
        String p = "";

        if (Long.parseLong(n) < 1 || Long.parseLong(n) > Math.pow(10, 1000000)) {
            throw new Exception("Parâmetro inválido!");
        } else if (k < 1 || k > Math.pow(10, 5)) {
            throw new Exception("Parâmetro inválido!");
        } else {
            if (cache.cacheMap.size() > 0) {
                Object value = cache.get(n.concat(",").concat(k.toString()));
                if (value != null) {
                    return Integer.valueOf(value.toString());
                }
            }

            for (Integer i = 1; i <= k; i++) {
                p = p.concat(String.valueOf(n));
            }

            if (p.length() == 1)
                return Integer.valueOf(p);
            else {
                while (p.length() > 1) {
                    for (Integer j = 0; j < p.length(); j++) {
                        soma = Character.getNumericValue(p.charAt(j)) + soma;
                    }
                    p = String.valueOf(soma);
                    soma = 0l;
                }

                cache.put(n.concat(",").concat(k.toString()), p);
                return Integer.valueOf(p);
            }
        }
    }
}
